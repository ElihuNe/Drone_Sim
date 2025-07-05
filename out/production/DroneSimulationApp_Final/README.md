# Drone Simulation Interface – Summer Project 2025

## Overview

The Drone Simulation Interface is a comprehensive Java desktop application that interacts with a RESTful API for analyzing and visualizing simulated drone data. It provides a structured and user-friendly interface to retrieve, process, and display information on drone types, current drone states, and real-time dynamics.

This application was developed as part of the "Object-Oriented Programming with Java – Advanced Course" at Frankfurt University of Applied Sciences, supervised by Prof. Dr. Robin Müller-Bady.

---

## Objectives

The main goal was to build a GUI-based Java application that demonstrates key concepts of modern object-oriented programming. The project connects to a drone simulation server via token authentication and retrieves data from different endpoints. Users can explore drone models, monitor active drones, and observe live flight data.

---

## Features

- Token-based API authentication via input panel
- Static Drone Catalog from /api/dronetypes/
- Drone Dashboard from /api/drones/ with derived statistics
- Flight Dynamics from /api/dronedynamics/ using pagination
- Manual refresh of data (refresh Button)
- Modular architecture following clean code principles
- Logging with java.util.logging for all key events and exceptions
- Fully multithreaded data loading to keep GUI responsive
- Derived metrics such as:
  - Battery level in %
  - Estimated remaining flight time
  - Average speed
  - Number of active drones

---

## How to Use

1. *Start the program* by running Main.java in your IDE.
2. *Enter your API token* and the base URL (e.g., https://dronesim.facets-labs.com/api/) in the Configuration Panel.
3. *Click on "Login"* to enable API communication.
4. Use the following tabs to navigate through the application:

   - *Drone Catalog*  
      View a list of all drone type models.
      View extra Information by hovering over a Drone name
      click on any drone type to see the corresponding available drones

   - *Drone Dashboard*  
     See all active drones with distributive data of Avg. Speed, Carriage Weight, Batteriy Distribution and most common drones
     Either in general for all drone types or for specific drone types that you can select through the drop-down menu.

   - *Flight Dynamics*  
     Displays all dynamic data for every drone in occurring order.

5. *Refresh: You can click the "Refresh" button refresh the data and start from the beginning.
6. If you need to change the token or URL later, simply restart the application.

---

## Installation

### Requirements

- Java Development Kit (JDK) 17 or higher
- Internet access or VPN (if required to reach the drone simulation server)
- Eclipse or IntelliJ (or any other Java IDE)
- API Token (provided by instructor)

### Setup

1. Download or clone the project ZIP file.
2. Import it into your IDE as a Java Project.
3. Ensure all .java files are compiled successfully.
4. Run the application by executing Main.java.

---

## Project Structure


app
├── Main.java # Application entry point
├── api/
│ └── API.java # API connection and token-based authentication
├── data/
│ ├── DataRepository.java # Central data handler
│ ├── FlightData.java # Data handler for droneDynamics
│ └── IDataProvider.java # Interface for data sources
├── logic/
│ └──DataAggregator.java # Calculates derived statistics
├── model/
│ ├── Drone.java
│ ├── DroneType.java
│ └── DroneDynamics.java # JSON-mapped data classes
├── ui/
│ ├── ConfigPanel.java
│ ├── DashboardPanel.java
│ ├── DroneCatalogPanel.java
│ ├── FlightDynamicsPanel.java
│ ├── HomePanel.java
│ └── MainFrame.java # Main window and navigation
└── util/
│ ├── AppException.java # Custom exception class
│ ├── ChartUtils.java # Graphical utilities (e.g. speed charts)
│ └──  Constants.java # API base URL and config settings
