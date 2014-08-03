package com.ansvia.zufaro

import org.specs2.mutable._

class ZufaroSpec extends Specification {
	
	"Hello" should {
		"match 5 characters" in {
			"hello".length must equalTo(5)
		}
	}
	
}
