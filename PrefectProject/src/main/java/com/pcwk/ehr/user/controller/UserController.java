package com.pcwk.ehr.user.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.pcwk.ehr.board.domain.BoardVO;
import com.pcwk.ehr.cmn.DTO;
import com.pcwk.ehr.cmn.MessageVO;
import com.pcwk.ehr.cmn.StringUtil;
import com.pcwk.ehr.code.domain.CodeVO;
import com.pcwk.ehr.code.service.CodeService;
import com.pcwk.ehr.mail.service.MailSendService;
import com.pcwk.ehr.user.domain.UserVO;
import com.pcwk.ehr.user.service.UserService;

@Controller
@RequestMapping("user")
public class UserController {
	final Logger LOG = LogManager.getLogger(getClass());
	
	@Autowired
	UserService  userService;
	
	@Autowired
	CodeService codeService;
	
	@Autowired
	private MailSendService mailService;
	
	//이메일 인증
	@RequestMapping(value="/mailCheck.do",method = RequestMethod.GET
			,produces = "application/json;charset=UTF-8"
			)
	@ResponseBody// HTTP 요청 부분의 body부분이 그대로 브라우저에 전달된다.
	public String mailCheck(HttpServletRequest request) {
		String email = request.getParameter("email");
		
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ mailCheck()                               │email:"+email);
		LOG.debug("└───────────────────────────────────────────┘");	
		return mailService.joinEmail(email);
		
	}
	
	//http://localhost:8080/ehr/user/idDuplicateCheck.do?userId='p8-03'
	@RequestMapping(value="/emailDuplicateCheck.do",method = RequestMethod.GET
			,produces = "application/json;charset=UTF-8"
			)
	@ResponseBody// HTTP 요청 부분의 body부분이 그대로 브라우저에 전달된다.
	public String emailDuplicateCheck(UserVO inVO) throws SQLException {
		String jsonString = "";  
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ emailDuplicateCheck()                     │inVO:"+inVO);
		LOG.debug("└───────────────────────────────────────────┘");		
					
		int flag = userService.emailDuplicateCheck(inVO);
		String message = "";
		if(0==flag) {
			message = inVO.getEmail()+"사용 가능한 아이디 입니다.";
		}else {
			message = inVO.getEmail()+"사용 불가한 아이디 입니다.";
		}
		MessageVO messageVO=new MessageVO(flag+"", message);
		jsonString = new Gson().toJson(messageVO);		
		LOG.debug("jsonString:"+jsonString);		
		return jsonString;
	}
	
	@RequestMapping(value="/moveToReg.do", method = RequestMethod.GET)
	public String moveToReg(Model model)throws SQLException {
		String view = "user/user_reg";
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ moveToReg                                 │");
		LOG.debug("└───────────────────────────────────────────┘");	
		
		//코드목록 조회 : 'EDUCATION','ROLE'
		Map<String, Object> codes =new HashMap<String, Object>();
		String[] codeStr = {"EDUCATION","ROLE"};
		
		codes.put("code", codeStr);
		List<CodeVO> codeList = this.codeService.doRetrieve(codes);
		
		List<CodeVO> educationList=new ArrayList<CodeVO>();
		List<CodeVO> roleList=new ArrayList<CodeVO>();
		
		
		for(CodeVO vo :codeList) {
			//EDUCATION
			if(vo.getMstCode().equals("EDUCATION")) {
				educationList.add(vo);
			}
			
			if(vo.getMstCode().equals("ROLE")) {
				roleList.add(vo);
			}	
			LOG.debug(vo);
		}
		
		LOG.debug("educationList");
		for(CodeVO vo :educationList) {
			LOG.debug(vo);
		}
		
		LOG.debug("roleList");
		for(CodeVO vo :roleList) {
			LOG.debug(vo);
		}
		
		
		model.addAttribute("education", educationList);
		
		model.addAttribute("role",roleList);
		
		
		
		return view;
	}
	
	//목록조회
	//http://localhost:8080/ehr/user/doRetrieve.do?searchDiv=10&searchWord=점심시간
	@RequestMapping(value="/doRetrieve.do", method = RequestMethod.GET)
	public String doRetrieve(UserVO  searchVO, HttpServletRequest req, Model model) throws SQLException {
		String view = "user/user_list";
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ doRetrieve                                │DTO:"+searchVO);
		LOG.debug("└───────────────────────────────────────────┘");				
		
		//검색 null처리 : null -> ""
		String searchDiv  = StringUtil.nvl(req.getParameter("searchDiv"));
		String searchWord = StringUtil.nvl(req.getParameter("searchWord"));
		searchVO.setSearchDiv(searchDiv);
		searchVO.setSearchWord(searchWord);
		
		
		//브라우저에서 숫자 : 문자로 들어 온다.
		//페이지 사이즈: null -> 10
		//페이지 번호: null -> 1
		String pageSize   = StringUtil.nvl(req.getParameter("pageSize"),"10");
		String pageNo     = StringUtil.nvl(req.getParameter("pageNo"),"1");
		
		long tPageNo      = Long.parseLong(pageNo);
		long tPageSize    = Long.parseLong(pageSize);

		long pageValue = (0==tPageNo)?1:tPageNo;
		searchVO.setPageNo(pageValue);

		
		long tPageSizeValue = (0 == tPageSize )?10:tPageSize;
		searchVO.setPageSize(tPageSizeValue);
		
		
		LOG.debug("pageSize:"+searchVO.getPageSize());	
		LOG.debug("pageNo:"+searchVO.getPageNo());
		
		LOG.debug("searchDiv:"+searchDiv);	
		LOG.debug("searchWord:"+searchWord);
		
		
		LOG.debug("searchVO:"+searchVO);
	
		//코드목록 조회 : 'PAGE_SIZE', 'USER_SEARCH', 'EDUCATION','ROLE'
		Map<String, Object> codes =new HashMap<String, Object>();
		String[] codeStr = {"PAGE_SIZE","USER_SEARCH", "EDUCATION","ROLE"};
		codes.put("code", codeStr);
		
		List<CodeVO> codeList = codeService.doRetrieve(codes);
		
		List<CodeVO> userSearchList = new ArrayList<CodeVO>();
		List<CodeVO> pageSizeList = new ArrayList<CodeVO>();
		List<CodeVO> educationList=new ArrayList<CodeVO>();
		List<CodeVO> roleList=new ArrayList<CodeVO>();
		
		
		for(CodeVO vo: codeList) {
			if(vo.getMstCode().equals("USER_SEARCH")) {
				userSearchList.add(vo);
			}
			
			if(vo.getMstCode().equals("PAGE_SIZE")) {
				pageSizeList.add(vo);
			}		
			
			//EDUCATION
			if(vo.getMstCode().equals("EDUCATION")) {
				educationList.add(vo);
			}
			
			if(vo.getMstCode().equals("ROLE")) {
				roleList.add(vo);
			}	
			LOG.debug(vo);
		}
		
		//검색조건 : USER_SEARCH
		model.addAttribute("userSearch", userSearchList);
		
		//페이지사이즈
		model.addAttribute("pageSize", pageSizeList);
		
		model.addAttribute("education", educationList);
		
		model.addAttribute("role",roleList);
		
		List<UserVO>  list = this.userService.doRetrieve(searchVO);
		
		//화면에 데이터 전달
		model.addAttribute("list", list);
		//검색조건
		model.addAttribute("searchVO", searchVO);
		
		//paging
		long bottomCount = 10;//바닥글
		long totalCnt    = 0;
		for(UserVO vo  :list) {
			if(totalCnt==0) {
				totalCnt = vo.getTotalCnt();
				break;
			}
		}
		
		
		String html = StringUtil.renderingPager(totalCnt, searchVO.getPageNo(), searchVO.getPageSize(), bottomCount, "/ehr/user/doRetrieve.do", "pageDoRerive");
		model.addAttribute("pageHtml", html);
		
		
		
		
		return view;
	}
	
	//수정
	@RequestMapping(value="/doUpdate.do",method = RequestMethod.POST
			,produces = "application/json;charset=UTF-8"
			)
	@ResponseBody// HTTP 요청 부분의 body부분이 그대로 브라우저에 전달된다.
	public String doUpdate(UserVO inVO) throws SQLException {
		String jsonString = "";
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ doUpdate()                                  │inVO:"+inVO);
		LOG.debug("└───────────────────────────────────────────┘");		
				
		int flag = this.userService.doUpdate(inVO);
		String message = "";
		if(1==flag) {
			message = inVO.getEmail()+"가 수정 되었습니다.";
		}else {
			message = inVO.getEmail()+"수정 실패";
		}
		MessageVO messageVO = new MessageVO(flag+"", message);
		jsonString = new Gson().toJson(messageVO);
		LOG.debug("jsonString:"+jsonString);	
						
		
		return jsonString;
	}
	
	
	
	//단건조회
	//value="/doSelectOne.do" => http://localhost:8080/ehr/user/doSelectOne.do
	//method = RequestMethod.GET => http://localhost:8080/ehr/user/doSelectOne.do?userId=p99-01
	//produces = "application/json;charset=UTF-8" => 데이터를 위 형식으로 생성
	//@ResponseBody : 반환값을 http의 응답의 본문으로 사용
	@RequestMapping(value="/doSelectOne.do", method = RequestMethod.GET)
	public String doSelectOne(UserVO inVO,HttpServletRequest req, Model model) throws SQLException, EmptyResultDataAccessException {
		String view = "user/user_mod";
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ doSelectOne()                             │inVO:"+inVO);
		LOG.debug("└───────────────────────────────────────────┘");	
		String userId = req.getParameter("email");
		LOG.debug("│ userId                                :"+userId);		
		
		UserVO outVO = this.userService.doSelectOne(inVO);
		LOG.debug("│ outVO                                :"+outVO);		

		model.addAttribute("outVO", outVO);
		
		//코드목록 조회 : 'EDUCATION','ROLE'
		Map<String, Object> codes =new HashMap<String, Object>();
		String[] codeStr = {"EDUCATION","ROLE"};
		
		codes.put("code", codeStr);
		List<CodeVO> codeList = this.codeService.doRetrieve(codes);
		
		List<CodeVO> educationList=new ArrayList<CodeVO>();
		List<CodeVO> roleList=new ArrayList<CodeVO>();
		
		
		for(CodeVO vo :codeList) {
			//EDUCATION
			if(vo.getMstCode().equals("EDUCATION")) {
				educationList.add(vo);
			}
			
			if(vo.getMstCode().equals("ROLE")) {
				roleList.add(vo);
			}	
			LOG.debug(vo);
		}
		
		LOG.debug("educationList");
		for(CodeVO vo :educationList) {
			LOG.debug(vo);
		}
		
		LOG.debug("roleList");
		for(CodeVO vo :roleList) {
			LOG.debug(vo);
		}
		
		
		model.addAttribute("education", educationList);
		
		model.addAttribute("role",roleList);
		
		return view;
	}
	
	
	//삭제
	//GET방식 요청: http://localhost:8080/ehr/user/doDelete.do?userId=pcwk
	@RequestMapping(value = "/doDelete.do", method = RequestMethod.GET
			,produces = "application/json;charset=UTF-8"
			)
	@ResponseBody
	public String doDelete(UserVO inVO,HttpServletRequest req) throws SQLException {
		String jsonString = "";
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ doDelete()                                │inVO:"+inVO);
		LOG.debug("└───────────────────────────────────────────┘");	
		String userId = req.getParameter("Email");
		LOG.debug("│ userId                                :"+userId);
		
		
		int flag = userService.doDelete(inVO);
		String message = "";
		
		if(1==flag) {
			message = inVO.getEmail()+"가 삭제 되었습니다.";
		}else {
			message = inVO.getEmail()+" 삭제 실패.";
		}
		
		MessageVO  messageVO=new MessageVO(String.valueOf(flag),message);
		jsonString = new Gson().toJson(messageVO);
		
		LOG.debug("jsonString:"+jsonString);		
		return jsonString;
	}
	
	//등록
	@RequestMapping(value="/doSave.do",method = RequestMethod.POST
			,produces = "application/json;charset=UTF-8"
			)
	@ResponseBody// HTTP 요청 부분의 body부분이 그대로 브라우저에 전달된다.
	public String doSave(UserVO inVO) throws SQLException{
		String jsonString = "";
		LOG.debug("┌───────────────────────────────────────────┐");
		LOG.debug("│ doSave()                                  │inVO:"+inVO);
		LOG.debug("└───────────────────────────────────────────┘");		
		
		
		int flag = userService.doSave(inVO);
		String message = "";
		
		if(1==flag) {
			message = inVO.getEmail()+"가 등록 되었습니다.";
		}else {
			message = inVO.getEmail()+"등록 실패.";
		}
		
		MessageVO messageVO=new MessageVO(flag+"", message);
		jsonString = new Gson().toJson(messageVO);
		LOG.debug("jsonString:"+jsonString);		
				
		return jsonString;
	}
	
	
}
