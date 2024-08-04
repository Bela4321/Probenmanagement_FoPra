# Probenmanagement Software

This repository contains the code and documentation for the project "Probenmanagement Software" as part of the Advanced Software Lab of the University of Marburg. The project aims to develop a comprehensive sample management system.

## Table of Contents

- [Introduction](#Introduction)
- [Features](#Featrues)
- [Installation](#Installation)
- [Usage](#Usage)
- [Imprint](#Imprint)
- [License](#License)

## Introduction

The Probenmanagement Software project is designed to facilitate efficient and effective management of samples in a laboratory setting. It includes functionalities for tracking metadata of certain studies, managing sample data, and creation of a comprehensive report at the end of the workflow..

## Features

- **Creating and managing studies**: Keep track of all the relevcant studies, including their metadata such as Start Date, End Date, Expected number of Subjects, Number of Sample Deliveries, the transmitter of the samples, as well as the sponsor of the study
- **Sample Delivery**: This feature lets the user upload an EXCEL-sheet including the data of the samples
- **Verify Sample Delivery**: Ensure that the uploaded data matches the expected one by comparing the barcodes of each sample
- **Manual Sample Delivery**: This feature allows the user to manually input the details of each sample if the EXCEL-sheet is not available.
- **Add Sample Analysis to Study**: Add a relevant analysis to the study if it is to be used for a sample in it
- **Add Analysis to Sample**: Add the analysis to the sample(s) of choice.
- **Create Workplace List**: Create a corresponding “WorkplaceList” which includes all the analyses and samples selected by the user
- **Enter Sample Analysis**: After the analysis is done, the results can be entered manually by the user
- **View Sample Analysis**: View the entered analysis results
- **Generate Report**: Automatically generate a comprehensive report including all sample data and analysis results
- **Read Results**: This feature lets the user upload an EXCEL-sheet that already includes all the relevant analysis results, in order to streamline the process of entering the analysis data

## Installation

The Usage of an IDE, such as IntelliJ IDEA is recommended when working with the code and repository

1. Clone the repository:
    
    ```bash
    git clone https://github.com/Bela4321/Probenmanagement_FoPra.git
    ```
    
2. The usage of the program requires a PostgreSQL database called sample_management. Using a tool like pgAdmin create a database and give it the name sample_management
3. Since the project uses Maven, after opening the project in your IDE, a notification should advise you to install all missing dependencies. If not, run 

```bash
mvn clean install 
```

1. Set the environment variables of the run configuration.

```bash
DB_USER=postgres;DB_PASSWORD=<your Database password>
```

---

## Usage

To start using the sample management system, follow these steps:

1. Start the application by running the SampleManagementApplication.Java
2. Open your web browser and navigate to:
    
    ```
    http://localhost:8080
    ```
    
3. Follow the on-screen instructions to begin managing your samples.

## Imprint
Institut für Virologie Marburg<br />
Immunmonitoring Labor<br />
Hans-Meerwein-Straße 2<br />
35043 Marburg<br />

Institutsleitung: Prof. Dr. Stephan Becker<br />
Laborleitung: Dr. Verena Krähling<br />

Kontakt:
Internet: https://www.uni-marburg.de/de/fb20/bereiche/ziei/virologie<br />
Telefon: ++49 (0)6421 2865158<br />
Email: immunmonitoring.labor@uni-marburg.de<br />

Gestaltung & technische Realisierung:
Bela Schinke: bela.schinke@gmail.com<br />
David Meyer:<br />
David Riemer: david.riemer07@gmail.com<br />
Edbert Faustine:<br />
Sayedfarhad Emami Dehcheshmeh:<br />
Mohsen Saleki:<br />

## License

This project is licensed under the MIT License. See the LICENSE file for more details.
