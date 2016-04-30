import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;


public class BatchCollector {

    public static <T, A> BatchCollectorImpl<T, A> collect(Function<List<T>, A> consumer, int batchSize) {
        return new BatchCollectorImpl<>(consumer, batchSize);
    }

    private static class FixedSizeCollector<T, A> {

        private final Function<List<T>, A> consumer;
        private final int batchSize;
        private List<T> list;
        private final List<A> upstream = new ArrayList<>();

        private FixedSizeCollector(Function<List<T>, A> consumer, int batchSize) {
            this.consumer = consumer;
            this.batchSize = batchSize;
            list = new ArrayList<>(batchSize);
        }

        public void add(T element) {
            if (list.size() >= batchSize)
                flush();

            list.add(element);
        }

        public void flush() {
            upstream.add(consumer.apply(list));
            list = new ArrayList<>(batchSize);
        }

        public List<A> finish() {
            flush();
            return upstream;
        }

        public FixedSizeCollector<T, A> combine(FixedSizeCollector<T, A> other) {
            other.list.forEach(this::add);
            this.upstream.addAll(other.upstream);
            return this;
        }
    }


    private static class BatchCollectorImpl<T, A> implements Collector<T, FixedSizeCollector<T, A>, List<A>> {

        private Function<List<T>, A> consumer;
        private final int batchSize;

        private BatchCollectorImpl(Function<List<T>, A> consumer, int batchSize) {
            this.consumer = consumer;
            this.batchSize = batchSize;
        }

        @Override
        public Supplier<FixedSizeCollector<T, A>> supplier() {
            return () -> new FixedSizeCollector<>(consumer, batchSize);
        }

        @Override
        public BiConsumer<FixedSizeCollector<T, A>, T> accumulator() {
            return FixedSizeCollector::add;
        }

        @Override
        public BinaryOperator<FixedSizeCollector<T, A>> combiner() {
            return FixedSizeCollector::combine;
        }

        @Override
        public Function<FixedSizeCollector<T, A>, List<A>> finisher() {
            return FixedSizeCollector::finish;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return CHARACTERISTICS;
        }
    }

    public static final Set<Collector.Characteristics> CHARACTERISTICS = Collections.unmodifiableSet(
      EnumSet.of(
        Collector.Characteristics.CONCURRENT,
        Collector.Characteristics.UNORDERED)
    );

}
