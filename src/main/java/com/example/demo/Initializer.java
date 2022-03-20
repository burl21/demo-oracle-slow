package com.example.demo;

import com.example.demo.domain.Activities;
import com.example.demo.domain.ActivitiesRepository;
import com.example.demo.domain.Document;
import com.example.demo.domain.DocumentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "db.populate")
public class Initializer {
	private final ActivitiesRepository activitiesRepository;
	private final DocumentsRepository documentsRepository;
	@Value("${db.how-many-records:300000}")
	private int recordsToInsert;

	@PostConstruct
	@Transactional
	public void initialize() throws InterruptedException {
		documents();
		activities();
	}

	private void documents() throws InterruptedException {
		var thread = new Thread(() -> {
			if (documentsRepository.count() == 0) {
				log.info("Documents insert started");
				var start = System.currentTimeMillis();
				var randomString = new RandomString();
				var random = new Random();
				var bulk = new ArrayList<Document>(1000);
				IntStream.range(1, recordsToInsert).forEach(i -> {
					var doc = new Document(
							null,
							String.join(".", randomString.nextString(), i % 2 == 0 ? "txt" : "pdf").toLowerCase(),
							random.nextInt(recordsToInsert),
							new Date()
					);
					bulk.add(doc);

					if (i % 1000 == 0 || i == recordsToInsert) {
						documentsRepository.saveAll(bulk);
						bulk.clear();
					}
				});
				log.info("Documents completed in {}ms", System.currentTimeMillis() - start);
			}
		});
		thread.join();
		thread.start();
	}

	private void activities() throws InterruptedException {
		var thread = new Thread(() -> {
			if (activitiesRepository.count() == 0) {
				log.info("Activities insert started");
				var start = System.currentTimeMillis();
				var random = new Random();
				var bulk = new ArrayList<Activities>(1000);
				IntStream.range(1, recordsToInsert).forEach(i -> {
					var doc = new Activities(
							null,
							random.nextInt(16) + 1,
							random.nextInt(16) + 1,
							(i > recordsToInsert - 30_000) ? null : (long) random.nextInt(recordsToInsert)
					);
					bulk.add(doc);

					if (i % 1000 == 0 || i == recordsToInsert) {
						activitiesRepository.saveAll(bulk);
						bulk.clear();
					}
				});
				log.info("Activities completed in {}ms", System.currentTimeMillis() - start);
			}
		});
		thread.join();
		thread.start();
	}
}
