/**
 * SPDX-License-Identifier: MIT
 */
package io.github.arthurc.autotest.testplan;

import com.fasterxml.jackson.annotation.JsonValue;

public record TestId(@JsonValue String value) {
}
