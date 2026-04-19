package com.londongemsapp.domain.classifier

import com.londongemsapp.domain.model.ClassificationResult

interface CategoryClassifier {
    fun classify(
        subreddit: String,
        title: String,
        body: String,
        flair: String?
    ): ClassificationResult
}
