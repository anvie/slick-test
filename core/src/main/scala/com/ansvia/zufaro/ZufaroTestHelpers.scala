package com.ansvia.zufaro

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import java.util.UUID

/**
 * Author: robin
 * Date: 8/25/14
 * Time: 8:13 PM
 *
 */
trait ZufaroTestHelpers {

    def genRandomString = {
        Base64.encodeBase64String(DigestUtils.md5Hex(UUID.randomUUID().toString).getBytes("UTF-8")).replace("=","")
    }
}

