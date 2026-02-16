package bbitai.config;


import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.Converter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@ComponentScan({"hu.avhga.g3.lib"})
@EntityScan("bbitai.domain")
@EnableJpaRepositories("bbitai.repository")
@EnableTransactionManagement
public class PsConfiguration {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

//	@Bean
//	ModelMapper modelMapper() {
//		ModelMapper modelMapper = new ModelMapper();
//
//		Converter<Enrollment, Long> enrollMentToId = context -> {
//			Enrollment source = context.getSource();
//			if (source!=null) {
//				return source.getId();
//			}
//			return null;
//		};
//
//		TypeMap<Grade, bbitai.api.model.Grade> grademapper = modelMapper
//				.createTypeMap(Grade.class, bbitai.api.model.Grade.class);
//
//		grademapper.addMappings(mapper ->
//				mapper.using(enrollMentToId).map(Grade::getEnrollment, bbitai.api.model.Grade::setEnrollment)
//		);
//
//
//
//
//		Converter<List<Grade>, List<Long>> gradeListToGradeValues = context -> {
//			List<Grade> source = context.getSource();
//			if (source != null&&!source.isEmpty()) {
//				return source.stream().map(g->g.getValue().longValue()).toList();
//			}
//			return new ArrayList<Long>();
//		};
//
//		TypeMap<Enrollment, bbitai.api.model.Enrollment> enrollmentMapper = modelMapper
//				.createTypeMap(Enrollment.class, bbitai.api.model.Enrollment.class);
//
//		enrollmentMapper.addMappings(mapper ->
//				mapper.using(gradeListToGradeValues).map(Enrollment::getGrades, bbitai.api.model.Enrollment::setGrades)
//		);
//
//		return modelMapper;
//	}
}