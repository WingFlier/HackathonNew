package dev.team.gradius.hackathon.Path;

import java.util.List;

public interface DirectionFinderListener
{
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
