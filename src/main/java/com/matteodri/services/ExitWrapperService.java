package com.matteodri.services;

import org.springframework.stereotype.Service;

/**
 * Class to add a layer in order to mock System.exit so it can be asserted in tested.
 *
 * @author Matteo Dri 01 Nov 2019
 */
@Service
public class ExitWrapperService {

    public void exit(int status) {
        System.exit(status);
    }
}
