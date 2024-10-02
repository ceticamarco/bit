package com.ceticamarco.bits.ApiResult;

import java.util.List;

public record ApiSuccess<T>(List<T> list) implements ApiResult { }
