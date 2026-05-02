package com.memowave.app.domain.algorithm

object FSRSConfig {
    const val REQUEST_RETENTION = 0.95

    val DEFAULT_PARAMS = listOf(
        0.212,  // w0  — initial stability for Again
        1.2931, // w1  — initial stability for Hard
        2.3065, // w2  — initial stability for Good
        8.2956, // w3  — initial stability for Easy
        6.4133, // w4  — initial difficulty base
        0.8334, // w5  — initial difficulty exponent
        3.0194, // w6  — difficulty delta scale
        0.001,  // w7  — mean reversion weight
        1.8722, // w8  — recall stability factor
        0.1666, // w9  — stability decay exponent
        0.796,  // w10 — retrievability factor
        1.4835, // w11 — forget stability base
        0.0614, // w12 — forget difficulty exponent
        0.2629, // w13 — forget stability power
        1.6483, // w14 — forget retrievability factor
        0.6014, // w15 — hard penalty
        1.8729, // w16 — easy bonus
        0.5425, // w17 — short-term stability exponent
        0.0912, // w18 — short-term offset
        0.0658, // w19 — short-term stability power
        0.1542, // w20 — decay
    )
}
