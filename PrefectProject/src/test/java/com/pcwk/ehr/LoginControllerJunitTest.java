package com.pcwk.ehr;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.pcwk.ehr.cmn.MessageVO;
import com.pcwk.ehr.cmn.PcwkLogger;
import com.pcwk.ehr.user.dao.UserDao;
import com.pcwk.ehr.user.domain.UserVO;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class) //스프링 테스트 컨텍스트 프레임웤그의 JUnit의 확장기능 지정
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"
		,"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginControllerJunitTest implements PcwkLogger {
	
	@Autowired
	WebApplicationContext webApplicationContext;
	
	//브라우저 대역
	MockMvc  mockMvc;
	
	List<UserVO> users;
	UserVO searchVO;
	
	@Autowired
	UserDao  dao;
	
	@Before
	public void setUp() throws Exception {
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ setUp()                                   │");		
		LOG.debug("└───────────────────────────────────────────┘");		
		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		users = Arrays.asList(
				 new UserVO("pohomen@naver.com-01", "김진수-01", "4321_01","01012341234","대졸","11")
				,new UserVO("pohomen@naver.com-02", "김진수-02", "4321_02","01012341234","대졸","11")
				,new UserVO("pohomen@naver.com-03", "김진수-03", "4321_03","01012341234","대졸","11")
				,new UserVO("pohomen@naver.com-04", "김진수-04", "4321_04","01012341234","대졸","11")
				,new UserVO("pohomen@naver.com-05", "김진수-05", "4321_05","01012341234","대졸","11")
			);
			
		searchVO = new UserVO();
		searchVO.setEmail("pohomen@naver.com");		
	}

	@Test
	public void doLogin()throws Exception{
		//1.데이터 삭제
		//2.데이터 입력
		//3.login
		
		//1
		dao.doDelete(users.get(0));
		dao.doDelete(users.get(1));
		dao.doDelete(users.get(2));
		dao.doDelete(users.get(3));
		dao.doDelete(users.get(4));
		
		assertEquals(0,dao.getCount(searchVO));
		
		//2
		int flag = dao.doSave(users.get(0));
		assertEquals(1, flag);
		assertEquals(1,dao.getCount(searchVO));		
		
		flag = dao.doSave(users.get(1));
		assertEquals(1, flag);
		assertEquals(2,dao.getCount(searchVO));		
		
		flag = dao.doSave(users.get(2));
		assertEquals(1, flag);
		assertEquals(3,dao.getCount(searchVO));		
		
		flag = dao.doSave(users.get(3));
		assertEquals(1, flag);
		assertEquals(4,dao.getCount(searchVO));	
		
		flag = dao.doSave(users.get(4));
		assertEquals(1, flag);
		assertEquals(5,dao.getCount(searchVO));				
		
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ doLogin()                                 │");		
		LOG.debug("└───────────────────────────────────────────┘");		
		
		MockHttpServletRequestBuilder  requestBuilder = 
				MockMvcRequestBuilders.post("/login/doLogin.do")
				.param("email",        users.get(0).getEmail())
				.param("password",      users.get(0).getPassword());
				
		ResultActions resultActions=this.mockMvc.perform(requestBuilder).andExpect(status().isOk());
		String result = resultActions.andDo(print()).andReturn().getResponse().getContentAsString();
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ result                                    │"+result);		
		LOG.debug("└───────────────────────────────────────────┘");		
				
		MessageVO messageVO = new Gson().fromJson(result, MessageVO.class);
		assertEquals(String.valueOf(30), messageVO.getMsgId());
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ messageVO                                 │"+messageVO);		
		LOG.debug("└───────────────────────────────────────────┘");					
				
	}
	
	@Test
	public void beans() {
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ beans()                                   │");		
		
		LOG.debug("webApplicationContext:"+webApplicationContext);	
		LOG.debug("mockMvc:"+mockMvc);	
		LOG.debug("dao:"+dao);	
		
		LOG.debug("└───────────────────────────────────────────┘");
		
		assertNotNull(webApplicationContext);
		assertNotNull(mockMvc);
		assertNotNull(dao);
		
	}

}
