/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.domain;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author joanr
 */
@Component
@Profile("test_user")
public class UserRepositoryTest
{

    private final UserRepository userRepository;

    public UserRepositoryTest(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadBookTestData()
    {

    }

}
