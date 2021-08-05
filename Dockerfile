FROM mozilla/sbt:8u232_1.3.13

RUN mkdir /code
COPY . /code/
WORKDIR /code

ENTRYPOINT ["sbt"]
CMD ["compile", "test", "run"]