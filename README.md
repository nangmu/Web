# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 모든 기본 요구사항 완료.

### 배운거
* Junit사용에 익숙치 않을 때 Test 메소드를 일일이 만드는게 비효율적으로 느껴졌었다. 익숙해지니 기존 테스트코드 없이 디버깅할때와 비교해서 시간 효율이 굉장히 좋아짐을 느낀다. 디버깅을 한참 하다보면, 머리가 복잡해져서 내가 무엇을 하고 있는지 헷갈릴때가 많은데 그런 문제들이 크게 줄었다. 또한 오래전에 작성한 코드들도 테스트 코드를 훑어보기만 하면 금방 기억해낼 수 있어서 좋았다.

* Log를 사용함으로써 여러가지 좋은 점을 느낄 수 있었다. 
 	1. 디버깅시 System.out.println()을 적고 지우고를 매우 빈번하게 사용하게 되는데 Log를 사용함으로써 빈도를 줄일 수 있었다.
 	2. xml로 출력하고자 하는 기본 포맷을 지정할 수 있다. 변경시 xml만 변경하면 되기 때문에 편리하다.
 	3. 기존 기본 입출력을 사용하지 않음으로써 자원낭비를 최소화 ??
 	4. 배포시, 디버깅을 위해 적어놓은 test코드들을 일일이 찾아서 전부 지워야 하는데 반에, log는 xml설정 변경(error, debug... )
 	을 통해 쉽게 수정 가능하다.
 
* Git GitHub 사용에 익숙해 졌다. 다만, 혼자 작업해서 그런지 분산 버전 관리 툴의 장점을 크게 느끼지 못했다. 스냅샷의 이점 정도만 느꼈다.
* 리팩토링을 잘하자
 
### 5장 마무리 - HTTP WEB SERVER 문제점
* 동적인 html을 만들기가 복잡하고 많은 코딩을 필요로 한다.
* HTTP요청과 응답헤더, 본문 처리와 같은데 시간을 투자함으로써 정작 중요한 비지니스 로직에 집중할 수 없다.
---> 이 두가지 문제점을 해결하기 위해 자바 진영에서 표준으로 정한 것이 서블릿 컨테이너와 서블릿/JSP 이다.

### Servlet/JSP
* HTTP의 클라이언트 요청과 응답에 대한 표준을 정해 놓은 것을 서블릿이라 한다.
* 서블릿 표준에 대한 구현을 담당하고 있으며 앞에서 구현한 웹 서버가 서블릿 컨테이너 역할과 같다.
* 서블릿 컨테이너는 서버가 시작할 때 서블릿 인스턴스 생성, 요청URL과 생성한 Controller 인스턴스를 연결시켜 놓고, 클라이언트에서 요청이 오면 요청 URL에 해당하는 서블릿을 찾아 서블릿에 모든 작업을 위임.
* 서블릿컨테이너 시작==> 클래스패스에 있는 Servlet인터페이스를 구현하는 서블릿 클래스를 찾음 ==> @WebServlet설정을 통해 요청 URL과 서블릿 매핑 
	==> 서블릿 인스턴스 생성 ==> init()메소드를 호출해 초기화 -> 클라이언트 요청이 있을때까지 대기 ==> 요청이 있을 경우 요청 URL에 해당하는 서블릿을 찾아 	service() 메소드 호출 ==> 서블릿 컨테이너 종료시 서블릿 컨테이너가 관리하는 모든 서블릿의 destroy() 메소드 호출해 소멸
(생성,초기화,서비스,소멸,멀티쓰레딩,설정파일,JSP지원 등등)

