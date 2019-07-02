/*
 * Copyright 2014-2016 Key Bridge LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alchitry.labs.hardware.usb.ftdi.enums;

/**
 * Number of stop bits for ftdi_set_line_property()
 *
 * @author Jesse Caulfield
 */
public enum LineStopbit {

  STOP_BIT_1(0), STOP_BIT_15(1), STOP_BIT_2(2);
  private final int stopbit;

  private LineStopbit(int stopbit) {
    this.stopbit = stopbit;
  }

  public int getStopbit() {
    return stopbit;
  }

}
