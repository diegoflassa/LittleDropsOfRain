package app.web.diegoflassa_site.littledropsofrain.data.helpers

import java.util.concurrent.TimeUnit

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SpecificTimeout(val duration: Int, val unit: TimeUnit)
