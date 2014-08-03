package com.ansvia.zufaro.exception

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 1:49 AM
 *
 */



class ZufaroException(msg:String, code:Int) extends Throwable(msg)

case class InsufficientBalanceException(msg:String) extends ZufaroException(msg:String, 701)
case class AlreadyInvestedException(msg:String) extends ZufaroException(msg:String, 702)

