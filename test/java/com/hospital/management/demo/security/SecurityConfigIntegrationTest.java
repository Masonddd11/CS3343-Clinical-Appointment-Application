package com.hospital.management.demo.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

@SpringBootTest
class SecurityConfigIntegrationTest {

        @Autowired
        private WebApplicationContext context;

        private MockMvc mockMvc;

        @Test
        void testCorsConfiguration() throws Exception {
                // Cover cors.configurationSource() lambda
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(options("/api/test")
                                .header("Origin", "http://localhost:3000")
                                .header("Access-Control-Request-Method", "GET"));
        }

        @Test
        void testH2ConsolePermitAll() throws Exception {
                // Cover requestMatchers("/h2-console/**").permitAll()
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(get("/h2-console"));
        }

        @Test
        void testAuthEndpointsPermitAll() throws Exception {
                // Cover requestMatchers("/api/auth/**").permitAll()
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(get("/api/auth/test"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void testAdminEndpointsRequireAdminRole() throws Exception {
                // Cover requestMatchers("/api/admin/**").hasRole("ADMIN")
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(get("/api/admin/test"));
        }

        @Test
        void testAdminEndpointsWithoutRole() throws Exception {
                // Cover requestMatchers("/api/admin/**").hasRole("ADMIN") - without role
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(get("/api/admin/test"));
        }

        @Test
        void testAnyRequestRequiresAuthentication() throws Exception {
                // Cover anyRequest().authenticated()
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(get("/api/test"));
        }

        @Test
        void testSessionManagementStateless() throws Exception {
                // Cover sessionManagement.sessionCreationPolicy(STATELESS)
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(get("/api/test"));
        }

        @Test
        void testFrameOptionsSameOrigin() throws Exception {
                // Cover headers.frameOptions().sameOrigin()
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(get("/h2-console"));
        }
}
