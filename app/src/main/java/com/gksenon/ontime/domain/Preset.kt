package com.gksenon.ontime.domain

import java.util.UUID
import kotlin.time.Duration

data class Preset(val id: UUID, val duration: Duration)
