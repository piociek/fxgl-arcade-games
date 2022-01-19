package com.piociek.fxgl.spaceinvaders.component;

import com.almasb.fxgl.entity.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ScoreComponent extends Component {

    @Getter
    private int score;
}
