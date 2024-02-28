/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.vt.metrics;

/**
 *
 * @author joanr
 */
public record CharacterIdStatistics(
    Long requestNumber,
    Long maximumConcurrentRequest,
    Long minimumRequestTime,
    Long maximumRequestTime,
    Double mean,
    Double deviation
) {}

