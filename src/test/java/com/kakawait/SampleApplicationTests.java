package com.kakawait;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class SampleApplicationTests {

    @LocalServerPort
    private int port;

    @Test
    public void sync() {
        TestRestTemplate restTemplate = new TestRestTemplate("administrator", "s3cr3t");
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port + "/sync", String.class);
        assertThat(entity.getHeaders()).containsEntry("X-Auth", Collections.singletonList("administrator"))
                                       .containsKey("X-Principal");
    }

    @Test
    public void async() {
        TestRestTemplate restTemplate = new TestRestTemplate("administrator", "s3cr3t");
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port + "/async", String.class);
        assertThat(entity.getHeaders()).containsEntry("X-Auth", Collections.singletonList("administrator"))
                                       .containsKey("X-Principal");
    }

}
