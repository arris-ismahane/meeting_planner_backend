package com.sme.meeting_planner;

import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import com.sme.meeting_planner.config.JwtUtils;

@Import({ JwtUtils.class })
@WithMockUser
public class BasicControllerTest {

}
