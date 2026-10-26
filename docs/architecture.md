# Architecture

`BreakerGame` advances autonomous physics at 120 fixed steps per second. The View interpolates positions for display. Circle/rectangle contacts remove real bricks; durable bricks require additional hits. The predictive paddle has finite travel speed and reaction time, so misses and relaunches occur. Cleared boards advance to a new formation. Multiple balls collide independently with the same live brick board. Impact particles and trail history have fixed bounds.

`BreakerOptions` validates eight choices and per-choice/all randomization without Android dependencies. `BreakerSceneView` draws ceramic gradients, restrained edge lighting, ball trails, impact fragments and a slowly moving board at the actual View dimensions. It never requests a forced 4K buffer or draws gameplay text.

Preview and Dream use the same scene. Pausing stops Choreographer callbacks; resuming resets frame timing, avoiding a large catch-up jump. Detaching releases callbacks. A new process constructs a new game from persisted options. SettingsActivity and SettingsProvider adapt Android preferences; no runtime SDK or third-party dependency is bundled.
