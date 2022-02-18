package com.group.jwtSample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JwtSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtSampleApplication.class, args);
	}

}


/*
	JWT
	Json 객체을 사용해서 정보들을 저장하고 있는 Web Token

	Header : Signature를 해싱하기 위한 알고리즘 정보들이 담겨짐
	          ㄴ 토큰의 유효성을 검증을 위한 문자열

	Payload : 서버와 클라이언트가 주고받는. 시스템에서 실제로 사용될 정보에 대한 내용들을 담고있음


	JWT의 장점
	- 중앙의 인증 서버, 데이터 스토어에 대한 의존성이 없고, 시스템 수평 확장 유리
	- Base64 URL Safe Encoding -> URL, Cookie, Header 모두 사용 가능

	단점
	- Payload의 정보가 많아지면 네트워크 사용량 증가, 데이터 설계 고려 필요
	- 토큰이 클라이언트에 저장, 서버에서 클라이언트의 토큰을 조작할 수 없음

*/