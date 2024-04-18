import React from 'react';
import { useCallback } from "react";
import Particles from "react-tsparticles";
import { loadSlim } from "tsparticles-slim";

const Design = () => {
  const particlesInit = useCallback(async engine => {
    console.log(engine);
    await loadSlim(engine);
  }, []);

  const particlesLoaded = useCallback(async container => {
    await console.log(container);
  }, []);

  return (
    <div>
      <Particles
        id="tsparticles"
        init={particlesInit}
        loaded={particlesLoaded}
        options={{
        //   background: {
        //     color: {
        //       value: "#0d47a1",
        //     },
        //   },
          fullScreen: {
            enable: true,
            
          },
          fpsLimit: 60,
          interactivity: {
            detectsOn: "window",
            events: {
              onClick: {
                enable: true,
                mode: "push",
              },
              onHover: {
                enable: true,
                mode: "",
              },
              resize: true,
            },
            modes: {
              push: {
                quantity: 5,
                rate: {
                  delay: 0.8,
                },
              },
            },
          },
          particles: {
            color: {
              value: "#ffffff",
            },
            move: {
              direction: "none",
              enable: true,
              outModes: {
                default: "bounce",
              },
              random: false,
              speed: 2,
            },
            
            number: {
              density: {
                enable: true,
                area: 800,
              },
              value: 600,
            },
            opacity: {
              value: 0.5,
            },
            shape: {
              type: "circle",
            },
            size: {
              value: 0.6,
            },
          },
          detectRetina: true,
        }}
      />
    </div>
  );
};

export default Design;