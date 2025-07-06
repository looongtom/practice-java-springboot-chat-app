package com.satvik.satchat;

import com.satvik.satchat.service.IFilesStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SatchatApplication implements CommandLineRunner {
  @Resource IFilesStorageService storageService;

  public static void main(String[] args) {
    SpringApplication.run(SatchatApplication.class, args);
  }

  @Override
  public void run(String... arg) throws Exception {
    //    storageService.deleteAll();
    storageService.init();
  }
}
