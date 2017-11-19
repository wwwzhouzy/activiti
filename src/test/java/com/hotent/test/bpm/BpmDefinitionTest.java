package com.hotent.test.bpm;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;

import com.hotent.test.BaseTestCase;
public class BpmDefinitionTest extends BaseTestCase{
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource 
	TaskService taskService;
	
	//流程引擎对象  
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();  

	
	@Test
	public void testDeploy() throws IOException{//部署流程
		InputStream is=readXmlFile();
		Assert.assertNotNull(is);
		//发布流程
		Deployment deployment=repositoryService.createDeployment().addInputStream("bpmn20.xml", is).name("holidayRequest").deploy();
		Assert.assertNotNull(deployment);
		System.out.println("deployId:" + deployment.getId());
		//查询流程定义
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
		
		Long businessKey=new Double(1000000*Math.random()).longValue();
		//启动流程
		runtimeService.startProcessInstanceById(processDefinition.getId(),businessKey.toString());
		//查询任务实例
		List<Task> taskList=taskService.createTaskQuery().processDefinitionId(processDefinition.getId()).list();
		Assert.assertNotNull(taskList==null);
		Assert.assertTrue(taskList.size()>0);
		for(Task task:taskList){
			System.out.println("task name is " + task.getName() + " ,task key is " + task.getTaskDefinitionKey()+"流程实例ID："+processDefinition.getId());
		}
	}
	
	@Test
	public void testQueryAllWaitTasks() throws IOException{//查询所有代办任务
		//查询任务实例
		List<Task> taskList=taskService.createTaskQuery().list();
		Assert.assertNotNull(taskList==null);
		Assert.assertTrue(taskList.size()>0);
		for(Task task:taskList){
			System.out.println("任务名称:" + task.getName() + ",处理人：" + task.getAssignee()+",流程实例ID："+task.getProcessInstanceId()+",任务ID:"+task.getId());
		}
	}
	
	/**完成我的任务*/  
	@Test  
	public void completeMyPersonalTask(){  
		//查询任务实例
		List<Task> taskList=taskService.createTaskQuery().list();
		Assert.assertNotNull(taskList==null);
		Assert.assertTrue(taskList.size()>0);
		int i = 0;
		for(Task task:taskList){
			i++;
			task.setAssignee(i+"");
			//任务ID  
			String taskId = task.getId(); 
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userid", i+"");
			taskService.complete(taskId,map); //与正在执行的任务管理相关的Service  
			System.out.println("任务名称:" + task.getName() + ",处理人：" + task.getAssignee()+",流程实例ID："+task.getProcessInstanceId()+",任务ID:"+task.getId());
		}
		
		
	}  
	
	/**查询指定人的代办任务*/  
	@Test  
	public void testQueryWaitTask(){  
		//查询任务实例
		List<Task> taskList=taskService.createTaskQuery().list();
		Assert.assertNotNull(taskList==null);
		Assert.assertTrue(taskList.size()>0);
		for(Task task:taskList){
			System.out.println("任务名称:" + task.getName() + ",处理人：" + task.getAssignee()+",流程实例ID："+task.getProcessInstanceId()+",任务ID:"+task.getId());
		}
		
		
	}

	
	public InputStream readXmlFile() throws IOException{
		String filePath="holidayRequest.bpmn";
		return Class.class.getClass().getResource("/"+filePath).openStream();
	}
}
