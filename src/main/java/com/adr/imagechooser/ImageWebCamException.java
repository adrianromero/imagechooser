//    AwesomeIcon is a library to select and display images control for JavaFX
//    Copyright (C) 2015 Adrián Romero Corchado.
//
//    This file is part of ImageChooser
//
//     Licensed under the Apache License, Version 2.0 (the "License");
//     you may not use this file except in compliance with the License.
//     You may obtain a copy of the License at
//     
//         http://www.apache.org/licenses/LICENSE-2.0
//     
//     Unless required by applicable law or agreed to in writing, software
//     distributed under the License is distributed on an "AS IS" BASIS,
//     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//     See the License for the specific language governing permissions and
//     limitations under the License

package com.adr.imagechooser;

/**
 *
 * @author adrian
 */
public class ImageWebCamException extends Exception {

    /**
     * Creates a new instance of <code>ImageWebCamException</code> without
     * detail message.
     */
    public ImageWebCamException() {
    }

    /**
     * Constructs an instance of <code>ImageWebCamException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ImageWebCamException(String msg) {
        super(msg);
    }
}
