<%@page import="com.pcwk.ehr.cmn.StringUtil"%>
<%@page import="com.pcwk.ehr.board.domain.BoardVO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="CP" value="${pageContext.request.contextPath}" scope="page" />
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/WEB-INF/cmn/header.jsp"></jsp:include>
<meta charset="EUC-KR">
<title>Insert title here</title>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>

<script>
$(document).ready(function () {
	$('#mail-Check-Btn').click(function() {
		const email = $('#userEmail1').val() + $('#userEmail2').val(); // 이메일 주소값 얻어오기!
		console.log('완성된 이메일 : ' + email); // 이메일 오는지 확인
		const checkInput = $('.mail-check-input') // 인증번호 입력하는곳 
		
		$.ajax({
			type : 'get',
			url : '/ehr/user/mailCheck.do?email='+email, // GET방식이라 Url 뒤에 email을 뭍힐수있다.
			success : function (data) {
				console.log("data : " +  data);
				checkInput.attr('disabled',false);
				code =data;
				alert('인증번호가 전송되었습니다.')
			}			
		}); // end ajax
	}); // end send eamil

	// 인증번호 비교 
	// blur -> focus가 벗어나는 경우 발생
	$('.mail-check-input').blur(function () {
		const inputCode = $(this).val();
		const $resultMsg = $('#mail-check-warn');
		
		if(inputCode == code){
			$resultMsg.html('인증번호가 일치합니다.');
			$resultMsg.css('color','green');
			$('#mail-Check-Btn').attr('disabled',true);
			$('#userEamil1').attr('readonly',true);
			$('#userEamil2').attr('readonly',true);
			$('#userEmail2').attr('onFocus', 'this.initialSelect = this.selectedIndex');
	         $('#userEmail2').attr('onChange', 'this.selectedIndex = this.initialSelect');
		}else{
			$resultMsg.html('인증번호가 불일치 합니다. 다시 확인해주세요!.');
			$resultMsg.css('color','red');
		}
	});

	});
	
function emailDuplicateCheck(){
	   console.log("-emailDuplicateCheck()-");	
	   let email1 = document.querySelector("#userEmail1").value;
	   let email2 = document.querySelector("#userEmail2").value;
	   let email = email1 + email2;
    if(eUtil.isEmpty(email) == true){
        alert('이메일을 입력 하세요.');
        //$("#email").focus();//사용자 email에 포커스
        document.querySelector("#userEmail1").focus();
        document.querySelector("#userEmail2").focus();
        return;
    }
    
    $.ajax({
        type: "GET",
        url:"/ehr/user/emailDuplicateCheck.do",
        asyn:"true",
        dataType:"json", /*return dataType: json으로 return */
        data:{
            email: email
        },
        success:function(data){//통신 성공
            console.log("success data:"+data);
            //let parsedJSON = JSON.parse(data);
            if("1" === data.msgId){
                alert(data.msgContents);
                document.querySelector("#emailCheck").value = 0;
            }else{
                alert(data.msgContents);
                document.querySelector("#emailCheck").value = 1;
            }
                    
        },
        error:function(data){//실패시 처리
            console.log("error:"+data);
        },
        complete:function(data){//성공/실패와 관계없이 수행!
            console.log("complete:"+data);
        }
    });
    
    
}

function doSave(){
    console.log("----------------------");
    console.log("-doSave()-");
    console.log("----------------------");
    
   let email1 = document.querySelector("#userEmail1").value;
   let email2 = document.querySelector("#userEmail2").value;
   let email = email1 + email2;
	   
    //javascript
    console.log("javascript email:"+email);
    /* console.log("javascript ppl_input:"+document.querySelector(".ppl_input").value);    */
    
    if(eUtil.isEmpty(email) == true){
 	   alert('아이디를 입력 하세요.');
 	   //$("#email").focus();//사용자 id에 포커스
 	   document.querySelector("#userEmail1").focus();
       document.querySelector("#userEmail2").focus();
 	   return;
    }
    
    if(eUtil.isEmpty(document.querySelector("#name").value) == true){
        alert('이름을 입력 하세요.');
        //$("#email").focus();//사용자 email에 포커스
        document.querySelector("#name").focus();
        return;
    }       
    
    
    if(eUtil.isEmpty(document.querySelector("#password").value) == true){
        alert('비밀번호를 입력 하세요.');
        //$("#email").focus();//사용자 email에 포커스
        document.querySelector("#password").focus();
        return;
    } 
    
    if(eUtil.isEmpty(document.querySelector("#tel").value) == true){
        alert('전화번호을 입력 하세요.');
        //$("#email").focus();//사용자 email에 포커스
        document.querySelector("#tel").focus();
        return;
    }      
    
    if(eUtil.isEmpty(document.querySelector("#education").value) == true){
        alert('학력을 입력 하세요.');
        //$("#email").focus();//사용자 email에 포커스
        document.querySelector("#education").focus();
        return;
    }

    if(eUtil.isEmpty(document.querySelector("#role").value) == true){
        alert('역할을 입력 하세요.');
        //$("#email").focus();//사용자 email에 포커스
        document.querySelector("#role").focus();
        return;
    }      

    
    if(document.querySelector("#emailCheck").value == '0'){
        alert('이메일 중복체크를 수행 하세요.');
        //$("#email").focus();//사용자 email에 포커스
        document.querySelector("#emailCheck").focus();
        return;
    }
    
    //confirm
    if(confirm("등록 하시겠습니까?")==false)return;
    
    
    $.ajax({
        type: "POST",
        url:"/ehr/user/doSave.do",
        asyn:"true",
        dataType:"html",
        data:{
     	   email:email,
     	   name: document.querySelector("#name").value,
     	   password: document.querySelector("#password").value,
     	   tel: document.querySelector("#tel").value,
     	   edu: document.querySelector("#education").value,
     	   role: document.querySelector("#role").value,
        },
        success:function(data){//통신 성공     
            console.log("success data:"+data);
           //data:{"msgId":"1","msgContents":"dd가 등록 되었습니다.","no":0,"totalCnt":0,"pageSize":0,"pageNo":0}
           let parsedJSON = JSON.parse(data);
           if("1" === parsedJSON.msgId){
         	  alert(parsedJSON.msgContents);
         	  moveToList();
           }else{
         	  alert(parsedJSON.msgContents);
           }
        
        },
        error:function(data){//실패시 처리
            console.log("error:"+data);
        },
        complete:function(data){//성공/실패와 관계없이 수행!
            console.log("complete:"+data);
        }
    });
}

function moveToList(){
	   console.log("----------------------");
	   console.log("-moveToList()-");
	   console.log("----------------------");
	   
	   window.location.href = "/ehr/user/doRetrieve.do";
}

</script>
</head>
<body>
	
	
	<div class="container">
         <!-- 제목 -->
	    <div class="row">
	        <div class="col-lg-12">
	            <h1 class="page-header">회원등록</h1>
	        </div>
	    </div>    
	    <!--// 제목 ----------------------------------------------------------------->
	    <!-- 버튼 -->
	    <div class="row justify-content-end">
	        <div class="col-auto">
		       <input type="button" class="btn btn-primary" value="등록" id="doSave"      onclick="window.doSave();">
		       <input type="button" class="btn btn-primary" value="목록" id="moveToList"  onclick="window.moveToList();">
	        </div>
	    </div>
        <!--// 버튼 ----------------------------------------------------------------->
	     
	     <!-- 회원 등록영역 -->  
	     <div>
	       <form action="#" name="userRegFrm">
	       
	       
	       <!-- 이메일 본인 인증 -->
	       <div class="form-group email-form">
			<label for="email">이메일</label>
				 <div class="input-group">
					<input type="text" class="form-control" name="userEmail1" id="userEmail1" placeholder="이메일" >
					<select class="form-control" name="userEmail2" id="userEmail2" >
					<option>@naver.com</option>
					<option>@daum.net</option>
					<option>@gmail.com</option>
					<option>@hanmail.com</option>
					 <option>@yahoo.co.kr</option>
					</select>
				</div>   
			<div class="input-group-addon">
			 <input type="button" class="btn btn-primar" value="등록" id="emailDuplicateCheck"      onclick="window.emailDuplicateCheck();">
				<button type="button" class="btn btn-primary" id="mail-Check-Btn">본인인증</button>
			</div>
				<div class="mail-check-box">
			<input class="form-control mail-check-input" placeholder="인증번호 6자리를 입력해주세요!" disabled="disabled" maxlength="6">
			</div>
				<span id="mail-check-warn"></span>
			</div>
			
	           <%-- email중복체크 수행 여부 확인:0(미수행),1(수행) --%>
	           <input type="hidden" name="emailCheck" id="emailCheck" value="0">
               <div class="mb-3"> <!--  아래쪽으로  여백 -->
                   <label for="name" class="form-label">이름</label>
                   <input type="text"  class="form-control"  name="name" id="name" placeholder="이름을 입력 하세요." size="20"  maxlength="21">
               </div>	
               <div class="mb-3">
                   <label for="password" class="form-label">비밀번호</label>
                   <input type="password"  class="form-control"  name="password" id="password" placeholder="비밀번호를 입력 하세요." size="20"  maxlength="30">
               </div>                 
               <div class="mb-3">
                   <label for="tel" class="form-label">전화번호</label>
                   <input type="text"  class="form-control numOnly" name="tel" id="tel" placeholder="전화번호를 입력하세요" size="20"  maxlength="11">
               </div>    
               <div class="mb-3">     
                   <label for="edu" class="form-label">학력</label>
                   <div class="col-auto">
                    <select id="education" name="education">
                        <!-- 검색 조건 옵션을 동적으로 생성 -->
                         <c:forEach items="${education}" var="vo">
					        <option value="${vo.detCode}">${vo.detName}</option>
					    </c:forEach>
                    </select>
                	</div>
               </div>
    
               <div class="mb-3">
                   <label for="role" class="form-label">역할</label>
                   <div class="col-auto">
                    <select id="role" name="role">
                        <!-- 검색 조건 옵션을 동적으로 생성 -->
                         <c:forEach items="${role}" var="vo">
					        <option value="${vo.detCode}">${vo.detName}</option>
					    </c:forEach>
                    </select>
                	</div>
               </div>                                                        
	       </form>
	     </div>
	     <!--// 회원 등록영역 ------------------------------------------------------>
	     <jsp:include page="/WEB-INF/cmn/footer.jsp"></jsp:include>    
     </div>
</body>
</html>