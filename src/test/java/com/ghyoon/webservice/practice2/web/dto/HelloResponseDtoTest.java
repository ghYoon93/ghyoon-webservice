package com.ghyoon.webservice.practice2.web.dto;


import com.ghyoon.webservice.practice2.web.dto.HelloResponseDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloResponseDtoTest {
    @Test
    public void 롬복_기능_테스트(){
        //given
        String name= "사과";
        int amount= 3000;

        //when
        HelloResponseDto dto = new HelloResponseDto(name, amount);

        //then
        assertThat(dto.getName()).isEqualTo("사과");
        assertThat(dto.getAmount()).isEqualTo(3000);
    }
}
