# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# NO CHECKED-IN PROTOBUF GENCODE
# source: price.proto
# Protobuf Python Version: 5.29.0
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import descriptor_pool as _descriptor_pool
from google.protobuf import runtime_version as _runtime_version
from google.protobuf import symbol_database as _symbol_database
from google.protobuf.internal import builder as _builder
_runtime_version.ValidateProtobufRuntimeVersion(
    _runtime_version.Domain.PUBLIC,
    5,
    29,
    0,
    '',
    'price.proto'
)
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from google.protobuf import empty_pb2 as google_dot_protobuf_dot_empty__pb2


DESCRIPTOR = _descriptor_pool.Default().AddSerializedFile(b'\n\x0bprice.proto\x12\x18\x63om.rahim.proto.protobuf\x1a\x1bgoogle/protobuf/empty.proto\"D\n\x11GoldPriceResponse\x12\x0e\n\x06source\x18\x01 \x01(\t\x12\r\n\x05price\x18\x02 \x01(\x02\x12\x10\n\x08\x64\x61tetime\x18\x03 \x01(\t2g\n\x10GoldPriceService\x12S\n\x0cGetGoldPrice\x12\x16.google.protobuf.Empty\x1a+.com.rahim.proto.protobuf.GoldPriceResponseB\x02P\x01\x62\x06proto3')

_globals = globals()
_builder.BuildMessageAndEnumDescriptors(DESCRIPTOR, _globals)
_builder.BuildTopDescriptorsAndMessages(DESCRIPTOR, 'price_pb2', _globals)
if not _descriptor._USE_C_DESCRIPTORS:
  _globals['DESCRIPTOR']._loaded_options = None
  _globals['DESCRIPTOR']._serialized_options = b'P\001'
  _globals['_GOLDPRICERESPONSE']._serialized_start=70
  _globals['_GOLDPRICERESPONSE']._serialized_end=138
  _globals['_GOLDPRICESERVICE']._serialized_start=140
  _globals['_GOLDPRICESERVICE']._serialized_end=243
# @@protoc_insertion_point(module_scope)
