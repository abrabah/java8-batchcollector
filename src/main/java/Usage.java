import java.util.List;
import java.util.stream.IntStream;

/**
 * The batch collector lets you perform operations on a list of
 * Java-Stream elements instead of performing operation on
 * single elements at a time. This is particularly helpful when sending
 * stream elements to a database or some other end point which benefits
 * from sending elements in batches.
 */

public class Usage {


    public static void main(String[] args) {
        final List<Long> integers = IntStream.range(0, 4_000_000).boxed()
          .collect(BatchCollector.collect(
            Usage::sumIntegers, 1000)
          );

        System.out.println(integers); // [499500, 1499500, 2499500, 3499500, 4499500, 5499500,  ...]
    }


    static long sumIntegers(List<Integer> integers) {
        return integers.stream().mapToLong(a -> a).sum();
    }


}
