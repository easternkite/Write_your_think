<img src="https://user-images.githubusercontent.com/83625797/132083273-a7608015-11aa-4adc-92e1-eb49c0359b4b.png" width="200" >

# WtiteYourThink(롸쳐띵)  

바쁜 일상 속 장문의 일기를 쓸 시간이 없는 현대인들을 위한 짧고 간결하게 일상을 기록하는 모바일 어플리케이션.



## 👨‍💻 Technical Lead
* Firebase Auth를 활용한 구글, 페이스북 계정 로그인 및 계정 연동 기능 구현
* SQLite를 활용한 로컬 DB와 Firebase DB의 클라우드 DB를 상호 연동기능 (데이터 백업 기능) 구현
* RecyclerView를 사용한 일기 데이터를 일일 별로 조회할 수 있는 기능 구현
* Github 오픈소스 달력을 활용하여 어떤 날짜에 몇 건의 데이터가 입력되었는지 조회 가능
* BottomSheetDialog를 이용한 입력창 구현
* Firebase를 활용한 FCM(푸시 알림)기능 구현
* 글로벌 출시를 위한 영문화 작업
* 구글 플레이 스토어, 삼성 갤럭시 스토어 배포 및 유지보수 (회원 수 : 250명)
* Java -> Kotlin 사용 언어 변환 100% 완료

## 👱‍♂️ Project Lead  
* 구글 플레이스토어, 갤럭시스토어 동시 출시 및 유지보수
* 간트 차트를 이용한 프로젝트 일정관리 
* GitHub를 통한 개발 프로세스 기록 및 버전관리  

## ☑️ Problem Solving
* **P1** : Firebase DB만 쓰면 데이터를 읽거나 갱신할 때 딜레이가 생기는 문제
* **Sol1** : Firebase DB와 SQLite DB를 연동시켰다. 따라서 첫 로그인 시에만 Firebase 데이터를 불러와 SQLite DB에 저장하는 과정이 생긴다.  
  이는 처음에 1~2초정도의 대기시간이 있지만 상시 딜레이가 생기는 것 보다는 낫다는 평가를 받음
* **P2** : 갤럭시 스토어 출시 전 심사에서 한국어로만 구성된 앱이라서 반려됨
* **Sol2** :  Android Studio의 Translations Editor를 이용하여 한국이 아닌 지역에서는 영어로 자동 번역되도록 영문화 작업을 진행하였다. 후에 심사는 안정적으로 통과.

## 🖼️ Screen Shots
| 💻 로그인 화면| ❤️ 메인 화면|🗓️ 캘린더| 🖋️ 입력 창|
|---|---|---|---|
|<img src="https://user-images.githubusercontent.com/83625797/132083289-1affdb6c-0687-484a-bb82-af972ba41210.jpg" width = "200">|<img src="https://user-images.githubusercontent.com/83625797/132083293-be3a9018-912e-4760-882b-50126022b5da.jpg" width= "200">|<img src="https://user-images.githubusercontent.com/83625797/132083294-a54b4f5b-1081-4ac9-a2b3-e36169855112.jpg" width = "200">|<img src="https://user-images.githubusercontent.com/83625797/132083382-6f9edbf7-d10a-4007-b77b-e50ffb33de2e.jpg" width = "200">|





