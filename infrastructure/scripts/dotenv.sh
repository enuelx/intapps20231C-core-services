#!/usr/bin/python

import yaml

with open("./src/main/resources/application.yml") as f:
    y = yaml.safe_load(f)
    y['spring']['rabbitmq']['host'] = '$RABBITMQ_HOST'
    y['spring']['rabbitmq']['password'] = '$RABBITMQ_PASSWORD'
    y['spring']['rabbitmq']['port'] = '$RABBITMQ_PORT'
    y['spring']['rabbitmq']['username'] = '$RABBITMQ_USERNAME'
    print(yaml.dump(y, default_flow_style=False, sort_keys=False))