# Smart Pet Pad

사용자는 아두이노 기기와 블루투스 페어링을 통해 사용자의 홈 wifi의 아이디와 비밀번호를 입력하게 되고 아두이노가 wifi에 연결하면 사용자와 mqtt 통신을 통해 원격으로 이용할 수 있습니다.

![image](https://user-images.githubusercontent.com/77502035/230044297-64310f31-61e8-4963-b47f-aab74da288b6.png)

![image](https://user-images.githubusercontent.com/77502035/230044343-b6e9e167-a050-4ebb-a6b9-7e4700dbddcd.png)

![image](https://user-images.githubusercontent.com/77502035/230044358-bf73b53a-358a-4905-a35d-5a8dda71038c.png)

![image](https://user-images.githubusercontent.com/77502035/230044608-a215d740-84a2-4e05-b3f6-208ab3a289b2.png)

![image](https://user-images.githubusercontent.com/77502035/230045065-d5414e9d-a97a-430a-ae68-6fcea691ed25.png)
블루투스 연결 페이지 
로그인 다이얼로그에 홈 와이파이 ID PW를 입력하면 대기 상태에 있던 아두이노가 홈 wifi가 연결됩니다.


https://user-images.githubusercontent.com/77502035/230045682-9a127b01-35c0-407d-a587-b345eb68d711.mp4

강아지가 올라간 후 녹색 물체가 감지 된다면 패드의 모터가 돌아가며 배변이 교체되고 firebase와 연결된 안드로이드 어플이 사용자에게 관련된 정보를 푸쉬 알람으로 보내줍니다.

버튼을 누른다면 기계와는 상관없이 모터가 돌아가며 배변이 교체되고 해당 날짜를 기록합니다.



https://user-images.githubusercontent.com/77502035/230046240-991630ea-0cef-4810-9b7b-665cd407a5b9.mp4

모터 돌아간것 확인


전체 회로도
![회로도](https://user-images.githubusercontent.com/77502035/230046780-b78cfaf1-b476-428c-84d5-80f3816dc0b3.PNG)
