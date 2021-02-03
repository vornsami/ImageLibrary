/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.scene.layout.BorderPane;

/**
 *
 * @author Sami
 */
@FunctionalInterface
interface TagOperation {
    public abstract void operate(BorderPane base);
}
