package org.example.rpcprotocol;

public enum RequestType {
    LOGIN,
    LOGOUT,
    GET_CASES, GET_DONORS, GET_DONATIONS,
    ADD_DONATION, ADD_DONOR,
    GET_CASE, GET_DONOR_BY_NAME,
    UPDATE_SUM_IN_CASE;


    private RequestType() {

    }
}
