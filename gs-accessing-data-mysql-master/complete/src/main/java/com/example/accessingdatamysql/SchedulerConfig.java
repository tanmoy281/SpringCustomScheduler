package com.example.accessingdatamysql;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer, DisposableBean {

	@Autowired
	private UserRepository userRepository;
	String cronsExpressions = null;

	@PostConstruct
	private void abccc() {
		cronsExpressions = userRepository.findById(new Integer(3)).get().getEmail();
	}

	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

		Stream.of(StringUtils.split(cronsExpressions, "|")).forEach(cronExpression -> {

			Runnable runnable = () -> System.out.println("Trigger task executed at " + new Date());

			Trigger trigger = new Trigger() {

				@Override

				public Date nextExecutionTime(TriggerContext triggerContext) {

					// String newCronExpression = "*/1 * * * * *";
					// 1min: 0 0/1 * * * ?
					// 1sec: */1 * * * * *
					String newCronExpression = userRepository.findById(new Integer(3)).get().getEmail();

					if (!StringUtils.equalsAnyIgnoreCase(newCronExpression, cronsExpressions)) {
						cronsExpressions = newCronExpression;
						taskRegistrar.setTriggerTasksList(new ArrayList<TriggerTask>());
						configureTasks(taskRegistrar);
						taskRegistrar.destroy();
						taskRegistrar.setScheduler(executor);
						taskRegistrar.afterPropertiesSet();
						return null;
					}

					CronTrigger crontrigger = new CronTrigger(cronExpression);
					return crontrigger.nextExecutionTime(triggerContext);

				}

			};

			taskRegistrar.addTriggerTask(runnable, trigger);

		});
	}

	@Override
	public void destroy() throws Exception {
		if (executor != null) {
			executor.shutdownNow();
		}
	}
}