#!/usr/bin/groovy

// Kindly borrowed from: https://github.com/comquent/imperative-when
// MIT License
// Copyright 2018 by Comquent GmbH

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

def call(boolean condition, body) {
    def config = [:]
    body.resolveStrategy = Closure.OWNER_FIRST
    body.delegate = config

    if (condition) {
        body()
    } else {
        Utils.markStageSkippedForConditional(STAGE_NAME)
    }
}

