/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.wrapper.rws.property;

import java.util.Map;

import jp.co.ricoh.ssdk.sample.wrapper.common.ResponseBody;

public class UpdateLdapResponseBody extends Ldap implements ResponseBody {

    UpdateLdapResponseBody(Map<String, Object> values) {
        super(values);
    }

}
