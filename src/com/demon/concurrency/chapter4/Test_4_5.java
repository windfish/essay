package com.demon.concurrency.chapter4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 运行多个任务并处理第一个结果
 * ExecutorService.invokeAny() 接收一个任务列表，并返回第一个执行完毕且没有抛出异常的任务的执行结果
 * @author fish
 * @version 2016年7月21日 上午10:59:47
 */
public class Test_4_5 {

	//用过两种验证机制进行验证，一种通过就可以了
	public static void main(String[] args) {
		String name = "test";
		String password = "test";
		
		UserValidator ldapValidator = new UserValidator("LDAP");
		UserValidator dbValidator = new UserValidator("DB");
		
		TaskValidation ldapTask = new TaskValidation(ldapValidator, name, password);
		TaskValidation dbTask = new TaskValidation(dbValidator, name, password);
		
		List<TaskValidation> taskList = new ArrayList<TaskValidation>();
		taskList.add(ldapTask);
		taskList.add(dbTask);
		
		ExecutorService executor = Executors.newCachedThreadPool();
		String result = null;
		try {
			result = executor.invokeAny(taskList);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		System.out.println("Main: End of the Execution."+result);
	}

}

class UserValidator {
	private String name;

	public UserValidator(String name) {
		super();
		this.name = name;
	}
	
	public boolean validate(String name, String password){
		Random random = new Random();
		try {
			long duration = (long) (Math.random()*10);
			System.out.printf("Validator %s: Validating a user during %d seconds.\n",this.name,duration);
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			return false;
		}
		return random.nextBoolean();
	}

	public String getName() {
		return name;
	}
}

class TaskValidation implements Callable<String> {
	private UserValidator validator;
	private String user;
	private String password;

	public TaskValidation(UserValidator validator, String user, String password) {
		super();
		this.validator = validator;
		this.user = user;
		this.password = password;
	}

	@Override
	public String call() throws Exception {
		if(!validator.validate(user, password)){
			System.out.println(validator.getName()+": validate failed.");
			throw new Exception("Error validate user");
		}
		System.out.println(validator.getName()+": validate success.");
		return validator.getName();
	}
	
}
