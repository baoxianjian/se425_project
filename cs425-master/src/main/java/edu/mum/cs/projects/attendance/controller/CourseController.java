package edu.mum.cs.projects.attendance.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.mum.cs.projects.attendance.domain.StudentAttendance;
import edu.mum.cs.projects.attendance.domain.entity.AcademicBlock;
import edu.mum.cs.projects.attendance.domain.entity.CourseOffering;
import edu.mum.cs.projects.attendance.domain.entity.Student;
import edu.mum.cs.projects.attendance.service.AttendanceService;
import edu.mum.cs.projects.attendance.service.CourseService;
import edu.mum.cs.projects.attendance.service.StudentService;
import edu.mum.cs.projects.attendance.util.DateUtil;

@Controller
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CourseController {
	@Autowired
	CourseService courseService;

	@Autowired
	AttendanceService attendanceService;

	@Autowired
	StudentService studentService;

	@RequestMapping(value = "/courseOffering/list/{beginDate}", method = RequestMethod.GET)
	public String getAllCourse(@PathVariable("beginDate") String beginDate, Model model) {

		List<CourseOffering> courseOfferings = courseService.getCourseOfferings(beginDate);
		model.addAttribute("courseOfferings", courseOfferings);

		return "courseOfferings";
	}

	@RequestMapping(value = "/courseOffering/getrecord/{cofferingid}", method = RequestMethod.GET)
	public String getAttendanceRecords(@PathVariable("cofferingid") long cofferingid, Model model) {

		CourseOffering coffering = courseService.getCourseOfferingbyID(cofferingid);
		AcademicBlock block = courseService.getAcademicBlock(DateUtil.convertDateToString(coffering.getStartDate()));
		coffering.setBlock(block);
		List<StudentAttendance> studentAttendance = attendanceService.retrieveStudentAttendanceRecords(coffering);
		model.addAttribute("studentAttendance", studentAttendance);
		model.addAttribute("block", block);

		return "attendanceListStudent";
	}

	@RequestMapping(value = "/courseOffering/student/{cofferingid}", method = RequestMethod.GET)
	public String getAttendanceRecordsStudent(@PathVariable("cofferingid") long cofferingid, Model model) {

		CourseOffering coffering = courseService.getCourseOfferingbyID(cofferingid);

		AcademicBlock block = courseService.getAcademicBlock(DateUtil.convertDateToString(coffering.getStartDate()));
		coffering.setBlock(block);

		Student student = studentService.getStudentsById(StudentService.sampleStudentId);

		List<StudentAttendance> studentAttendance = attendanceService
					.retrieveStudentAttendanceRecords(coffering);
		
		if(studentAttendance == null){
			return "redirect:/my/courselist?attendance=none";			
		}
		
		studentAttendance = studentAttendance.stream().filter(a -> a.getStudent().equals(student)).collect(Collectors.toList());
		
		model.addAttribute("studentAttendance", studentAttendance);
		model.addAttribute("block", block);

		return "attendanceListStudent";
	}

	@RequestMapping(value = "/getallblocks", method = RequestMethod.GET)
	public String getAllAcadamicBlocks(Model model) {

		List<AcademicBlock> blocks = courseService.getAllAcademicBlock();
		model.addAttribute("blocks", blocks);

		return "academicblocks";
	}

}
