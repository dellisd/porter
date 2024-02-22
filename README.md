# porter

Make the work of fetching your datasets easier.

Imagine you're doing some data analysis that requires some large datasets. You might not be able to check it in to git,
and you don't want to write out instructions on where to find and download this data and ensure that it's saved to the
correct location for your analysis to work.

What if you just defined a _manifest_ of data and ran a single command that downloaded that data into the right place?

## Setup

TODO

## Data Manifest

```yaml
# porter.yaml
sources:
  - name: your_data.zip
    url: https://example.com/example.zip
```

## Usage

```shell
porter sync
```
