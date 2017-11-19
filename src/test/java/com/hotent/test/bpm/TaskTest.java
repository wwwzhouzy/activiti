package com.hotent.test.bpm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import com.hotent.test.BaseTestCase;

public class TaskTest  extends BaseTestCase{
	
	//流程引擎对象
	@Resource
	ProcessEngine processEngine;
	
	/**部署流程定义+启动流程实例*/
	@Test
	public void deployementAndStartProcess(){
		InputStream inputStreamBpmn = this.getClass().getResourceAsStream("/holidayRequest.bpmn");
		InputStream inputStreampng = this.getClass().getResourceAsStream("/holidayRequest.png");
		
		//部署流程定义
		Deployment deployment = processEngine.getRepositoryService()//
						.createDeployment()//创建部署对象
						.addInputStream("holidayRequest.bpmn", inputStreamBpmn)//部署加载资源文件
						.addInputStream("holidayRequest.png", inputStreampng)
						.name("个人任务演示")
						.deploy();
		System.out.println("部署ID："+deployment.getId());
		
		//启动流程实例
		/**
		 * 启动流程实例的同时，设置流程变量，使用流程变量的方式设置下一个任务的办理人
		 *     流程变量的名称，就是在task.bpmn中定义#{userID}的userID
		 *     流程变量的值，就是任务的办理人
		 */
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("userid", "周江霄");
		ProcessInstance pi = processEngine.getRuntimeService()//
				.startProcessInstanceByKey("myProcess",variables);//使用流程定义的key的最新版本启动流程
		System.out.println("流程实例ID："+pi.getId());
		System.out.println("流程定义的ID："+pi.getProcessDefinitionId());
	}
	
	/**查询我的个人任务*/
	@Test
	public void findPersonalTaskList(){
		//任务办理人
		String assignee = "周治尧";
		List<Task> list = processEngine.getTaskService()
					.createTaskQuery()
					.taskAssignee(assignee)//个人任务的查询
					.list();
		if(list!=null && list.size()>0){
			for(Task task:list){
				System.out.println("任务ID："+task.getId());
				System.out.println("任务的办理人："+task.getAssignee());
				System.out.println("任务名称："+task.getName());
				System.out.println("任务的创建时间："+task.getCreateTime());
				System.out.println("流程实例ID："+task.getProcessInstanceId());
				System.out.println("#######################################");
			}
		}
	}
	
	/**完成任务*/
	@Test
	public void completeTask(){
		List<Task> list = processEngine.getTaskService()
				.createTaskQuery()
				.list();
		if(list!=null && list.size()>0){
			for(Task task:list){
				/**
				 * 启动流程实例的同时，设置流程变量，使用流程变量的方式设置下一个任务的办理人
				 *     流程变量的名称，就是在task.bpmn中定义#{userID}的userID
				 *     流程变量的值，就是任务的办理人
				 */
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("userid", "周治尧");
				//任务ID
				String taskId = task.getId();
				processEngine.getTaskService()
								.complete(taskId,variables);
				System.out.println("完成任务："+taskId);
			}
		}
		
	}
}
