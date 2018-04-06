# 내가쓰는대로

안드로이드의 TextToSpeech를 이용해 글을 읽어주는 앱이다.

7개의 한국어 목소리와 영어, 일본어를 읽어줄 수 있다.

안드로이드의 기본 TTS 엔진을 이용하고 싶은 사람들은 참고하길 바란다.

## 주요 기능

1. 음성 합성
2. 읽기 속도
3. 음 높이
4. 목소리 종류
5. 언어(한국어, 영어, 일본어)



### 목소리 종류

>아직까지는 영어와 일본어에서 목소리 종류를 지정할 수는 없다.

기본적으로 Google TTS 엔진을 사용하면 여성의 목소리로 설정되어있다.

그러나 구글 번역에서 한국어 음성을 들어보면 남성의 목소리가 나온다.

남성의 목소리를 추가하는 방법은 다음과 같다.



목소리를 추가하는 방법은

`setVoice(Voice)`를 통해 할 수 있다.

파라미터로는 Voice 객체가 들어가게된다.

그래서 우선 Voice 객체를 만들어줘야한다.

```java
Voice voiceobj = new Voice(strVoice, Locale.KOREA, 1, 1, false, null);
```

strVoice에 들어가야할 문자열은 다음과 같다.

```
기본 목소리: 공백

여성 1: ko-kr-x-ism#female_1-local

여성 2: ko-kr-x-ism#female_2-local

여성 3: ko-kr-x-ism#female_3-local

남성 1: ko-kr-x-ism#male_1-local

남성 2: ko-kr-x-ism#male_2-local

남성 3: ko-kr-x-ism#male_3-local
```

내가 찾아본 결과로는 이게 다인 것 같다.

그 다음 `setVoice(voiceobj)`를 해주면 목소리가 적용된다.
