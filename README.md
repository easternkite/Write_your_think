<img src="https://user-images.githubusercontent.com/83625797/132083273-a7608015-11aa-4adc-92e1-eb49c0359b4b.png" width="200" >

# WtiteYourThink(롸쳐띵)  

바쁜 일상 속 장문의 일기를 쓸 시간이 없는 현대인들을 위한 짧고 간결하게 일상을 기록하는 모바일 어플리케이션.



## Technical Lead
* Android Native(JAVA)  
* RecyclerView를 이용한 SQLite 데이터 표시 및 수정, 삭제 기능 구현
* Firebase를 활용한 로그인 인증(Google, Facebook)
* SQLite DB와 Firebase DB 연동
* BottomSheetDialog를 이용한 입력창 구현
* 달력 구현 (GitHub 오픈소스 활용) : 날짜별로 일기 쓴 횟수 보여줌
* Firebase를 활용한 FCM(푸시알림) 기능 구현
* 영문화 작업으로 전 세계 사람들이 이용 가능하도록 구현

## Project Lead  
* 구글 플레이스토어, 갤럭시스토어 동시 출시 및 유지보수
* 간트 차트를 이용한 프로젝트 일정관리 
* GitHub를 통한 개발 프로세스 기록 및 버전관리  

## Problem Solving
* **P1** : Firebase DB만 쓰면 데이터를 읽거나 갱신할 때 딜레이가 생기는 문제
* **Sol1** : Firebase DB와 SQLite DB를 연동시켰다. 따라서 첫 로그인 시에만 Firebase 데이터를 불러와 SQLite DB에 저장하는 과정이 생긴다.  
  이는 처음에 1~2초정도의 대기시간이 있지만 상시 딜레이가 생기는 것 보다는 낫다는 평가를 받음
* **P2** : 갤럭시 스토어 출시 전 심사에서 한국어로만 구성된 앱이라서 반려됨
* **Sol2** :  Android Studio의 Translations Editor를 이용하여 한국이 아닌 지역에서는 영어로 자동 번역되도록 영문화 작업을 진행하였다. 후에 심사는 안정적으로 통과.

## Screen Shots
### -Login  
 <img src="https://user-images.githubusercontent.com/83625797/132083289-1affdb6c-0687-484a-bb82-af972ba41210.jpg" width = "200">  
 
### -Main  
<img src="https://user-images.githubusercontent.com/83625797/132083293-be3a9018-912e-4760-882b-50126022b5da.jpg" width= "200">  

### -달력
<img src="https://user-images.githubusercontent.com/83625797/132083294-a54b4f5b-1081-4ac9-a2b3-e36169855112.jpg" width = "200">  

### -입력창
<img src="https://user-images.githubusercontent.com/83625797/132083382-6f9edbf7-d10a-4007-b77b-e50ffb33de2e.jpg" width = "200">
