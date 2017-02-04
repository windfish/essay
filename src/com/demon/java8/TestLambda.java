package com.demon.java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class TestLambda {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("1","2","3","4","2","5");
		Collections.sort(names, (o1, o2) -> o1.compareTo(o2));
		System.out.println(names);
		
		
		String[] array = {"a", "b", "c"};
		for(int i : Lists.newArrayList(1,2,3)){
		  Stream.of(array).map(item -> Strings.padEnd(item, i, '@')).forEach(System.out::println);
		}
		
		List<Integer> nums = Lists.newArrayList(1,null,3,4,null,6);
		long l = nums.stream().filter(n -> n != null).count();
		System.out.println(nums+"==============="+l);
		
	}
	
	@Test
	public void testStreamGenerate(){
		Stream<Integer> integerStream = Stream.of(1,2,3,4,5); //Stream.of(1);
		integerStream.forEach(System.out::println);
		
		Stream.generate(Math::random).limit(10).forEach(System.out::println);
		
		Stream.iterate(10, i -> i+1).limit(5).forEach(System.out::println);
		
		Arrays.asList(1,2,3,4).stream().forEach(System.out::println);
	}
	
	@Test
	public void testConvertStream(){
		List<Integer> nums = Lists.newArrayList(1,1,null,2,3,4,null,5,6,7,8,9,10);
		System.out.println("sum is:"+nums.stream().filter(num -> num != null).
	    			distinct().mapToInt(num -> num * 2).
	                peek(System.out::println).skip(2).limit(4).sum());
	}
	
	@Test
	public void testCollect(){
		List<Integer> nums = Lists.newArrayList(1,1,null,2,3,4,null,5,6,7,8,9,10);
		List<Integer> result = nums.stream().filter(num -> num != null)
								.collect(
										() -> new ArrayList<Integer>(), 
										(list, item) -> list.add(item), 
										(list1, list2) -> list1.addAll(list2)
										);
		System.out.println(result);
		result = nums.stream().filter(num -> num != null).collect(Collectors.toList());
		System.out.println(result);
		
		result = nums.stream().filter(num -> num != null)
				.collect(
						ArrayList::new, 
						List::add, 
						(list1, list2) -> list1.addAll(list2)
						);
		System.out.println(result);
	}
	
	@Test
	public void testReduce(){
		List<Integer> ints = Lists.newArrayList(1,2,3,4,5,6,7,8,9,10);
	    System.out.println("ints sum is:" + ints.stream().reduce((sum, item) -> sum + item).get());
	}
	
}
