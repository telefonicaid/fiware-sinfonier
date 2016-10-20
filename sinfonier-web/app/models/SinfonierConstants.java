package models;

import play.Play;

public class SinfonierConstants extends Constants {

  public static class SinfonierUser {
    public static final String FIELD_TWITTER = Play.configuration.getProperty("SinfonierConstants.SinfonierUser.fieldTwitter", "twitter");
    public static final String FIELD_ORGANIZATION = Play.configuration.getProperty("SinfonierConstants.SinfonierUser.fieldOrganization", "organization");
    public static final String FIELD_TIME_ZONE = Play.configuration.getProperty("SinfonierConstants.SinfonierUser.fieldTimeZone", "timeZone");
    public static final String FIELD_WEB = Play.configuration.getProperty("SinfonierConstants.SinfonierUser.fieldWeb", "web");

    public static final String FORMAT_TIME_ZONE = Play.configuration.getProperty("SinfonierConstants.SinfonierUser.formatTimeZone", "(GMT+%d:%02d) %s");
  }

  public static class ModelCollection {
    public static final String FIELD_ID = Play.configuration.getProperty("SinfonierConstants.ModelCollection.fieldId", "_id");
  }

  public static class Module {
    public static final String COLLECTION_NAME = Play.configuration.getProperty("SinfonierConstants.Module.collectionName", "modules");

    public static final Integer MAX_WIRES = Integer.parseInt(Play.configuration.getProperty("SinfonierConstants.Module.maxWires", "10"), 10);
    public static final Integer LIMIT_TOP_MODULES = Integer.parseInt(Play.configuration.getProperty("SinfonierConstants.Module.limitTopModules", "5"), 10);
    public static final Integer MAX_PARALLELISM = Integer.parseInt(Play.configuration.getProperty("modules.maxParallelism", "5"), 10);

    public static final String FIELD_STATUS = Play.configuration.getProperty("SinfonierConstants.Module.fieldStatus", "status");
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.Module.fieldName", "name");
    public static final String FIELD_TYPE = Play.configuration.getProperty("SinfonierConstants.Module.fieldType", "type");
    public static final String FIELD_CATEGORY = Play.configuration.getProperty("SinfonierConstants.Module.fieldCategory", "category");
    public static final String FIELD_ICON = Play.configuration.getProperty("SinfonierConstants.Module.fieldIcon", "icon");
    public static final String FIELD_LANGUAGE = Play.configuration.getProperty("SinfonierConstants.Module.fieldLanguage", "language");
    public static final String FIELD_AUTHOR_ID = Play.configuration.getProperty("SinfonierConstants.Module.fieldAuthorId", "authorId");
    public static final String FIELD_CREATED = Play.configuration.getProperty("SinfonierConstants.Module.fieldCreatedAt", "created_at");
    public static final String FIELD_UPDATED = Play.configuration.getProperty("SinfonierConstants.Module.fieldUpdateAt", "updated_at");
    public static final String FIELD_TOPOLOGIES_COUNT = Play.configuration.getProperty("SinfonierConstants.Module.fieldTopologiesCount", "topologies_count");

    public static final String FIELD_RATINGS = Play.configuration.getProperty("SinfonierConstants.Module.fieldRatings", "ratings");
    public static final String FIELD_AVERAGE_RATE = Play.configuration.getProperty("SinfonierConstants.Module.fieldAverageRate", "average_rate");
    public static final String FIELD_COMPLAINS = Play.configuration.getProperty("SinfonierConstants.Module.complains", "complains");
    public static final String FIELD_VERSIONS = Play.configuration.getProperty("SinfonierConstants.Module.versions", "versions");

    public static final String STATUS_PENDING = Play.configuration.getProperty("SinfonierConstants.Module.status.pending", "pending");
    public static final String STATUS_DEV = Play.configuration.getProperty("SinfonierConstants.Module.status.dev", "developing");
    public static final String STATUS_PUBLISHED = Play.configuration.getProperty("SinfonierConstants.Module.status.published", "published");
    public static final String STATUS_DELETED = Play.configuration.getProperty("SinfonierConstants.Module.status.deleted", "deleted");
    public static final String STATUS_PRIVATE = Play.configuration.getProperty("SinfonierConstants.Module.status.private", "private");
    public static final String STATUS_PREDEFINED = Play.configuration.getProperty("SinfonierConstants.Module.status.predefined", "predefined");

    public static final String TYPE_SPOUT = Play.configuration.getProperty("SinfonierConstants.Module.type.spout", "spout");
    public static final String TYPE_BOLT = Play.configuration.getProperty("SinfonierConstants.Module.type.bolt", "bolt");
    public static final String TYPE_DRAIN = Play.configuration.getProperty("SinfonierConstants.Module.type.drain", "drain");
    public static final String TYPE_VARIABLE = Play.configuration.getProperty("SinfonierConstants.Module.type.variable", "variable");
    public static final String TYPE_COMMENT = Play.configuration.getProperty("SinfonierConstants.Module.type.comment", "comment");
    public static final String TYPE_OPERATOR = Play.configuration.getProperty("SinfonierConstants.Module.type.operator", "operator");

    public static final String LANG_JAVA = Play.configuration.getProperty("SinfonierConstants.Module.lang.java", "java");
    public static final String LANG_PYTHON = Play.configuration.getProperty("SinfonierConstants.Module.lang.python", "python");

    public static final String PATH_TO_SAVE = Play.configuration.getProperty("SinfonierConstants.Module.path", "tmp/module/");
    public static final String PATH_TO_SAVE_ICONS = Play.configuration.getProperty("SinfonierConstants.Module.path", "public/images/modules/icons/");
    public static final Integer LIMIT_COMPLAINS_NOTIFY_ADMIN = Integer.valueOf(Play.configuration.getProperty("limit.modules.complains", "3"));
    public static final String BLACKLIST = Play.configuration.getProperty("SinfonierConstants.Module.blacklistName", "sinfonier-backend,PythonBoltWrapper,BaseSinfonierBolt,BaseSinfonierDrain,PythonDrainWrapper,BaseSinfonierSpout,PythonSpoutWrapper");

  }

  public static class Version {
    public static final String FIELD_VERSION_TAG = Play.configuration.getProperty("SinfonierConstants.Version.fieldVersionTag", "versionTag");
    public static final String FIELD_VERSION_CODE = Play.configuration.getProperty("SinfonierConstants.Version.fieldVersionCode", "versionCode");
    public static final String FIELD_VERSION_ID = Play.configuration.getProperty("SinfonierConstants.Version.fieldVersionId", "versionId");
    public static final String FIELD_IS_DELETED = Play.configuration.getProperty("SinfonierConstants.Version.fieldIsDeleted", "isDeleted");
    public static final String FIELD_IS_VISIBLE = Play.configuration.getProperty("SinfonierConstants.Version.fieldIsDeleted", "isVisible");
  }

  public static class ModuleVersion {
    public static final String COLLECTION_NAME = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.collectionName", "moduleVersions");

    public static final Integer LIMIT_PENDING_MODULE_VERSIONS = Integer.valueOf(Play.configuration.getProperty("limit.modules.pending", "3"));

    public static final String FIELD_VERSION_TAG = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldVersionTag", "versionTag");
    public static final String FIELD_VERSION_CODE = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldVersionCode", "versionCode");
    public static final String FIELD_STATUS = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldStatus", "status");
    public static final String FIELD_MY_TOOLS = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.myTools", "my_tools");
    public static final String FIELD_SOURCE_TYPE = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldSourceType", "sourceType");
    public static final String FIELD_SOURCE_CODE = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldSourceCode", "sourceCode");
    public static final String FIELD_SOURCE_CODE_URL = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldSourceCodeUrl", "sourceCodeUrl");
    public static final String FIELD_DESCRIPTION = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldDescription", "description");
    public static final String FIELD_CREATED = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldCreatedAt", "created_at");
    public static final String FIELD_UPDATED = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldUpdateAt", "updated_at");
    public static final String FIELD_SINGLETON = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldSingleton", "singleton");
    public static final String FIELD_TOPOLOGIES_COUNT = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldTopologiesCount", "topologies_count");
    public static final String FIELD_TICK_TUPLE = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldTickTuple", "ticktuple");
    public static final String FIELD_FIELDS = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldFields", "fields");
    public static final String FIELD_LIBRARIES = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.fieldLibraries", "libraries");
    public static final String FIELD_CONTAINER = Play.configuration.getProperty("SinfonierConstants.ModuleVersion.container", "container");
    public static final String FIELD_BUILD_STATUS = Play.configuration.getProperty("SinfonierConstants.Module.buildStatus", "buildStatus");

    public static final String BUILD_STATUS_SUCCESS = Play.configuration.getProperty("SinfonierConstants.Module.buildStatus.success", "success");
    public static final String BUILD_STATUS_FAILURE = Play.configuration.getProperty("SinfonierConstants.Module.buildStatus.failure", "failure");

    public static final String SOURCE_TYPE_GIST = Play.configuration.getProperty("SinfonierConstants.Module.sourceType.gist", "gist");
    public static final String SOURCE_TYPE_TEMPLATE = Play.configuration.getProperty("SinfonierConstants.Module.sourceType.template", "template");
  }

  public static class TickTuple {
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.TickTuple.fieldName", "name");
    public static final String FIELD_LABEL = Play.configuration.getProperty("SinfonierConstants.TickTuple.fieldLabel", "label");
    public static final String FIELD_TYPE = Play.configuration.getProperty("SinfonierConstants.TickTuple.fieldType", "type");
    public static final String FIELD_REQUIRED = Play.configuration.getProperty("SinfonierConstants.TickTuple.fieldRequired", "required");
    public static final String FIELD_WIRABLE = Play.configuration.getProperty("SinfonierConstants.TickTuple.fieldWirable", "wirable");
  }

  public static class ModuleField {
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.ModuleField.fieldName", "name");
    public static final String FIELD_LABEL = Play.configuration.getProperty("SinfonierConstants.ModuleField.fieldLabel", "label");
    public static final String FIELD_TYPE = Play.configuration.getProperty("SinfonierConstants.ModuleField.fieldType", "type");
    public static final String FIELD_REQUIRED = Play.configuration.getProperty("SinfonierConstants.ModuleField.fieldRequired", "required");
    public static final String FIELD_WIRABLE = Play.configuration.getProperty("SinfonierConstants.ModuleField.fieldWirable", "wirable");
    public static final String FIELD_ELEMENT_TYPE = Play.configuration.getProperty("SinfonierConstants.ModuleField.fieldElementType", "elementType");
    public static final String FIELD_CHOICES = Play.configuration.getProperty("SinfonierConstants.ModuleField.fieldChoices", "choices");

    public static final String TYPE_LIST = Play.configuration.getProperty("SinfonierConstants.ModuleField.type.list", "list");
    public static final String TYPE_BOOL = Play.configuration.getProperty("SinfonierConstants.ModuleField.type.bool", "bool");
    public static final String TYPE_INTEGER = Play.configuration.getProperty("SinfonierConstants.ModuleField.type.integer", "integer");
    public static final String TYPE_NUMBER = Play.configuration.getProperty("SinfonierConstants.ModuleField.type.number", "number");
    public static final String TYPE_STRING = Play.configuration.getProperty("SinfonierConstants.ModuleField.type.string", "string");
    public static final String TYPE_URL = Play.configuration.getProperty("SinfonierConstants.ModuleField.type.url", "url");
  }

  public static class ElementType {
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.ElementType.fieldName", "name");
    public static final String FIELD_TYPE = Play.configuration.getProperty("SinfonierConstants.ElementType.fieldType", "type");
    public static final String FIELD_FIELDS = Play.configuration.getProperty("SinfonierConstants.ElementType.fieldFields", "fields");
    public static final String FIELD_SEPARATORS = Play.configuration.getProperty("SinfonierConstants.ElementType.fieldSeparators", "separators");
    public static final String FIELD_LABEL = Play.configuration.getProperty("SinfonierConstants.ElementType.fieldLabel", "label");
  }

  public static class ElementTypeField {
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.ElementTypeField.fieldName", "name");
    public static final String FIELD_TYPE = Play.configuration.getProperty("SinfonierConstants.ElementTypeField.fieldType", "type");
    public static final String FIELD_TYPE_INVITE = Play.configuration.getProperty("SinfonierConstants.ElementTypeField.fieldTypeInvite", "typeInvite");
    public static final String FIELD_LABEL = Play.configuration.getProperty("SinfonierConstants.ElementTypeField.fieldLabel", "label");
    public static final String FIELD_CHOICES = Play.configuration.getProperty("SinfonierConstants.ElementTypeField.fieldChoices", "choices");
  }

  public static class ModuleLibrary {
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.ModuleLibrary.fieldName", "name");
    public static final String FIELD_URL = Play.configuration.getProperty("SinfonierConstants.ModuleLibrary.fieldUrl", "url");
  }

  public static class Inappropriate {
    public static final String FIELD_COMMENT = Play.configuration.getProperty("SinfonierConstants.Inappropriate.fieldComment", "comment");
  }

  public static class Rating {
    public static final String FIELD_RATE = Play.configuration.getProperty("SinfonierConstants.Rating.fieldRate", "rate");
    public static final String FIELD_COMMENT = Play.configuration.getProperty("SinfonierConstants.AuthRatingsor.fieldComment", "comment");
  }

  public static class UserTimestamp {
    public static final String FIELD_USER_ID = Play.configuration.getProperty("SinfonierConstants.UserTiemstamp.fieldUserId", "userId");
    public static final String FIELD_TIMESTAMP = Play.configuration.getProperty("SinfonierConstants.UserTiemstamp.fieldTimestamp", "timestamp");
  }

  public static class OffsetPosition {
    public static final String FIELD_TOP = Play.configuration.getProperty("SinfonierConstants.OffsetPosition.top", "top");
    public static final String FIELD_RIGHT = Play.configuration.getProperty("SinfonierConstants.OffsetPosition.right", "right");
    public static final String FIELD_BOTTOM = Play.configuration.getProperty("SinfonierConstants.OffsetPosition.bottom", "bottom");
    public static final String FIELD_LEFT = Play.configuration.getProperty("SinfonierConstants.OffsetPosition.left", "left");
    public static final Integer DEFAULT_LEFT = Integer.parseInt(Play.configuration.getProperty("SinfonierConstants.OffsetPosition.defaultLeft", "82"), 10);
    public static final Integer YES_LEFT = Integer.parseInt(Play.configuration.getProperty("SinfonierConstants.OffsetPosition.defaultLeft", "76"), 10);
    public static final Integer NO_LEFT = Integer.parseInt(Play.configuration.getProperty("SinfonierConstants.OffsetPosition.defaultLeft", "96"), 10);
  }

  public static class DdConfig {
    public static final String FIELD_TYPE = Play.configuration.getProperty("SinfonierConstants.OffsetPosition.left", "type");
    public static final String FIELD_ALLOWED = Play.configuration.getProperty("SinfonierConstants.OffsetPosition.right", "allowedTypes");
  }

  public static class Terminal {
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.Terminal.name", "name");
    public static final String FIELD_N_MAX_WIRES = Play.configuration.getProperty("SinfonierConstants.Terminal.nMaxWires", "nMaxWires");
    public static final String FIELD_DIRECTIONS = Play.configuration.getProperty("SinfonierConstants.Terminal.direction", "direction");
    public static final String FIELD_POSITION = Play.configuration.getProperty("SinfonierConstants.Terminal.offsetPosition", "offsetPosition");
    public static final String FIELD_DD_CONFIG = Play.configuration.getProperty("SinfonierConstants.Terminal.ddConfig", "ddConfig");
    public static final Integer OFFSET_WITH_FIELDS = Integer.parseInt(Play.configuration.getProperty("SinfonierConstants.Terminal.offsetWithFields", "-15"), 10);
    public static final Integer OFFSET_WITHOUT_FIELDS = Integer.parseInt(Play.configuration.getProperty("SinfonierConstants.Terminal.offsetWithoutFields", "20"), 10);
  }

  public static class ModuleContainer {
    public static final String FIELD_TYPE = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.type", "type");
    public static final String FIELD_XTYPE = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.xtype", "xtype");
    public static final String FIELD_ICON = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.icon", "icon");
    public static final String FIELD_TERMINALS = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.terminals", "terminals");
    public static final String FIELD_ATTR = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.attributes", "attributes");
    public static final String FIELD_FIELDS = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.fields", "fields");
    public static final String FIELD_SINGLETON = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.singleton", "singleton");
    public static final String FIELD_DESCRIPTION = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.description", "description");
    public static final String FIELD_LANGUAGE = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.language", "language");
    public static final String FIELD_VERSION_CODE = Play.configuration.getProperty("SinfonierConstants.ModuleContainer.versionCode", "versionCode");
  }

  public static class Attributes {
    public static final String FIELD_ID = Play.configuration.getProperty("SinfonierConstants.Attributes.id", "abstractionId");
    public static final String FIELD_CLASS = Play.configuration.getProperty("SinfonierConstants.Attributes.class", "class");
  }

  public static class Topology {
    public static final String COLLECTION_NAME = Play.configuration.getProperty("SinfonierConstants.Topology.collectionName", "topologies");

    public static final String FIELD_ID = Play.configuration.getProperty("SinfonierConstants.Topology.fieldId", "_id");
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.Topology.fieldName", "name");
    public static final String FIELD_STATUS = Play.configuration.getProperty("SinfonierConstants.Topology.fieldStatus", "status");
    public static final String FIELD_SHARING = Play.configuration.getProperty("SinfonierConstants.Topology.fieldSharing", "sharing");
    public static final String FIELD_DESCRIPTION = Play.configuration.getProperty("SinfonierConstants.Topology.fieldDescription", "description");
    public static final String FIELD_CREATED = Play.configuration.getProperty("SinfonierConstants.Topology.fieldCreatedAt", "created_at");
    public static final String FIELD_UPDATED = Play.configuration.getProperty("SinfonierConstants.Topology.fieldUpdateAt", "updated_at");
    public static final String STATUS_ACTIVE = Play.configuration.getProperty("SinfonierConstants.Topology.status.active", "active");
    public static final String STATUS_DELETED = Play.configuration.getProperty("SinfonierConstants.Topology.status.deleted", "deleted");
    public static final String STATUS_RUNNING = Play.configuration.getProperty("SinfonierConstants.Topology.status.running", "running");
    public static final String STATUS_STOPPED = Play.configuration.getProperty("SinfonierConstants.Topology.status.stopped", "stopped");
    public static final String FIELD_AUTHOR_ID = Play.configuration.getProperty("SinfonierConstants.Topology.fieldAuthorId", "authorId");
    public static final String FIELD_CONFIG = Play.configuration.getProperty("SinfonierConstants.Topology.fieldConfig", "config");
    public static final String FIELD_TEMPLATE_ID = Play.configuration.getProperty("SinfonierConstants.Topology.fieldTemplateId", "template_id");
    public static final String TEMPLATE_NAME = "template";
    public static final Boolean SHARING_DEFAULT_VALUE = Boolean.parseBoolean(Play.configuration.getProperty("SinfonierConstants.Topology.sharingDefaultValue", "false"));
    
    public static final int TOPOLOGY_MAX_RESULTS_PAGE = Integer.parseInt(Play.configuration.getProperty("SinfonierConstants.Topology.pagination.maxResults", "5"));
  }

  public static class ModuleConfig {
    public static final String FIELD_POSITION = Play.configuration.getProperty("SinfonierConstants.ModuleConfig.position", "position");
    public static final String FIELD_XTYPE = Play.configuration.getProperty("SinfonierConstants.ModuleConfig.xtype", "xtype");
  }

  public static class Point {
    public static final String FIELD_ID = Play.configuration.getProperty("SinfonierConstants.Point.id", "moduleId");
    public static final String FIELD_ID_ANNOTATION = "moduleId";
    public static final String FIELD_TERMINAL = Play.configuration.getProperty("SinfonierConstants.Point.terminal", "terminal");
  }

  public static class Wire {
    public static final String FIELD_XTYPE = Play.configuration.getProperty("SinfonierConstants.ModuleConfig.xtype", "xtype");
    public static final String FIELD_SOURCES = Play.configuration.getProperty("SinfonierConstants.ModuleConfig.position", "src");
    public static final String FIELD_TARGET = Play.configuration.getProperty("SinfonierConstants.ModuleConfig.target", "tgt");
    public static final String FIELD_SOURCES_ANNOTATION = "src";
    public static final String FIELD_TARGET_ANNOTATION = "tgt";
  }

  public static class TopologyModule {
    public static final String FIELD_NAME = Play.configuration.getProperty("SinfonierConstants.TopologyModule.name", "name");
    public static final String FIELD_TYPE = Play.configuration.getProperty("SinfonierConstants.TopologyModule.type", "type");
    public static final String FIELD_LANGUAGE = Play.configuration.getProperty("SinfonierConstants.TopologyModule.fieldLanguage", "language");
    public static final String FIELD_VALUES = Play.configuration.getProperty("SinfonierConstants.TopologyModule.values", "value");
    public static final String FIELD_VALUES_ANNOTATION = "value";
    public static final String FIELD_MODULE_ID_ANNOTATION = "module_id";
    public static final String FIELD_MODULE_VERSION_ID_ANNOTATION = "module_version_id";
    public static final String FIELD_CONFIG = Play.configuration.getProperty("SinfonierConstants.TopologyModule.config", "config");
    public static final String FIELD_VERSION_CODE = Play.configuration.getProperty("SinfonierConstants.TopologyModule.versionCode", "versionCode");
    public static final String FIELD_PARALLELISMS = Play.configuration.getProperty("SinfonierConstants.TopologyModule.parallelisms", "parallelism");
  }

  public static class TopologyConfig {
    public static final String FIELD_WIRES = Play.configuration.getProperty("SinfonierConstants.TopologyConfig.wires", "wires");
    public static final String FIELD_MODULES = Play.configuration.getProperty("SinfonierConstants.TopologyConfig.modules", "modules");
    public static final String FIELD_PROPERTIES = Play.configuration.getProperty("SinfonierConstants.TopologyConfig.properties", "properties");
    public static final String FIELD_EXTRA_CONFIGURATION = Play.configuration.getProperty("SinfonierConstants.TopologyConfig.extraConfiguration", "extraConfiguration");
  }

  public static class UI {
    public static final String X_TYPE_FORM = Play.configuration.getProperty("SinfonierConstants.UI.xtype.form", "WireIt.FormContainer");
    public static final String X_TYPE_CONTAINER = Play.configuration.getProperty("SinfonierConstants.UI.xtype.container", "WireIt.Container");
    public static final String X_TYPE_BEZIER = Play.configuration.getProperty("SinfonierConstants.UI.xtype.bezier", "WireIt.BezierWire");
    public static final String X_TYPE_TEXTAREA = Play.configuration.getProperty("SinfonierConstants.UI.xtype.textarea", "WireIt.TextareaContainer");
  }

  public static class Drawer {
    public static final Boolean IS_ACTIVE_N_WORKERS = Boolean.parseBoolean(Play.configuration.getProperty("storm.option.topology_workers", "false"));
    public static final Boolean IS_ACTIVE_EXTRA_PARAMS = Boolean.parseBoolean(Play.configuration.getProperty("storm.option.topology_extra_params", "false"));
    public static final Boolean IS_ACTIVE_SPOUT_PENDING = Boolean.parseBoolean(Play.configuration.getProperty("storm.option.topology_max_spout_pending", "false"));
    public static final Boolean IS_ACTIVE_MSG_TIMEOUT = Boolean.parseBoolean(Play.configuration.getProperty("storm.option.topology_message_timeout_secs", "false"));
  }
}
