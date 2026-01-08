#!/bin/bash

curl --fail-with-body -X POST --data-binary @transactions-2023-01-11.csv http://localhost:5000/transactions
