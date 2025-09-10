package bench;

import linear.list.MyList;
import linear.list.arraylist.internal.ArrayListEx;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * JDK ArrayList vs 커스텀 ArrayListEx 성능 비교 벤치마크.
 */
@BenchmarkMode(Mode.AverageTime)               // 평균 시간 측정
@OutputTimeUnit(TimeUnit.NANOSECONDS)          // 결과 단위: ns
@State(Scope.Thread)                           // 스레드마다 독립 상태
public class ArrayListBench {

	@Param({"1000", "10000", "100000"})        // 입력 크기 N
	int N;

	List<Integer> jdkList;                     // JDK ArrayList
	MyList<Integer> myList;                    // 사용자 구현 ArrayListEx

	int[] randomIdx;                           // 랜덤 인덱스 배열

	@Setup(Level.Trial)
	public void setupRandom() {
		Random r = new Random(42);
		randomIdx = new int[Math.max(1024, N)];
		for (int i = 0; i < randomIdx.length; i++) {
			randomIdx[i] = r.nextInt(Math.max(1, N));
		}
	}

	@Setup(Level.Invocation)
	public void setupLists() {
		jdkList = new ArrayList<>(N);
		myList = new ArrayListEx<>();

		for (int i = 0; i < N; i++) {
			jdkList.add(i);
			myList.add(i);
		}
	}

	/* -------------------- Add (tail) -------------------- */

	@Benchmark
	public List<Integer> jdk_add_tail() {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < N; i++) list.add(i);
		return list;
	}

	@Benchmark
	public MyList<Integer> my_add_tail() {
		MyList<Integer> list = new ArrayListEx<>();
		for (int i = 0; i < N; i++) list.add(i);
		return list;
	}

	/* -------------------- Get random -------------------- */

	@Benchmark
	public void jdk_get_random(Blackhole bh) {
		int idx = randomIdx[(int)(System.nanoTime() & (randomIdx.length - 1))] % N;
		bh.consume(jdkList.get(idx));
	}

	@Benchmark
	public void my_get_random(Blackhole bh) {
		int idx = randomIdx[(int)(System.nanoTime() & (randomIdx.length - 1))] % N;
		bh.consume(myList.get(idx));
	}

	/* -------------------- Set random -------------------- */

	@Benchmark
	public void jdk_set_random(Blackhole bh) {
		int idx = randomIdx[(int)(System.nanoTime() & (randomIdx.length - 1))] % N;
		bh.consume(jdkList.set(idx, idx));
	}

	@Benchmark
	public void my_set_random(Blackhole bh) {
		int idx = randomIdx[(int)(System.nanoTime() & (randomIdx.length - 1))] % N;
		bh.consume(myList.set(idx, idx));
	}

	/* -------------------- Remove mid -------------------- */

	@Benchmark
	public void jdk_remove_mid(Blackhole bh) {
		int idx = jdkList.size() / 2;
		bh.consume(jdkList.remove(idx));
	}

	@Benchmark
	public void my_remove_mid(Blackhole bh) {
		int idx = myList.size() / 2;
		bh.consume(myList.remove(idx));
	}

	/* -------------------- Iterate -------------------- */

	@Benchmark
	public void jdk_iterate(Blackhole bh) {
		long sum = 0;
		for (int v : jdkList) sum += v;
		bh.consume(sum);
	}

	@Benchmark
	public void my_iterate(Blackhole bh) {
		long sum = 0;
		for (int v : myList) sum += v;
		bh.consume(sum);
	}
}
