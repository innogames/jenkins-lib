package com.innogames.jenkinslib.container

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@interface Value {

	String value();

}