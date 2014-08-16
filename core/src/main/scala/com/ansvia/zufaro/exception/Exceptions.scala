package com.ansvia.zufaro.exception

/**
 * Author: robin
 * Date: 8/4/14
 * Time: 1:49 AM
 *
 */



class ZufaroException(msg:String, val code:Int) extends Throwable(msg)

case class InsufficientBalanceException(msg:String) extends ZufaroException(msg:String, 701)
case class AlreadyInvestedException(msg:String) extends ZufaroException(msg:String, 702)
case class PermissionDeniedException(msg:String) extends ZufaroException(msg:String, 703)
case class InvalidParameterException(msg:String) extends ZufaroException(msg:String, 704)
case class NotSupportedException(msg:String) extends ZufaroException(msg:String, 705)
case class UnimplementedException(msg:String) extends ZufaroException(msg:String, 706)
case class NotExistsException(msg:String) extends ZufaroException(msg:String, 404)
case class IllegalStateException(msg:String) extends ZufaroException(msg:String, 707)

