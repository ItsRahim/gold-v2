"""CREATE price_sources table

Revision ID: 43c6c0315585
Revises: 
Create Date: 2025-03-21 13:11:48.728783

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision: str = '43c6c0315585'
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade():
    op.execute('CREATE SCHEMA IF NOT EXISTS python-api')

    op.create_table(
        'price_sources',
        sa.Column('id', sa.Integer(), autoincrement=True, nullable=False),
        sa.Column('name', sa.String(length=50), unique=True, nullable=True),
        sa.Column('endpoint', sa.String(length=50), nullable=True),
        sa.Column('url', sa.String(length=255), nullable=True),
        sa.Column('element', sa.String(length=500), nullable=True),
        sa.Column('is_active', sa.Boolean(), nullable=False),
        sa.PrimaryKeyConstraint('id'),
        schema='python-api'
    )
    op.execute("COMMENT ON TABLE python-api.price_sources IS 'Table to store information about price sources'")
    op.execute("COMMENT ON COLUMN python-api.price_sources.id IS 'Unique identifier for each source'")
    op.execute("COMMENT ON COLUMN python-api.price_sources.name IS 'Name of the price source'")
    op.execute("COMMENT ON COLUMN python-api.price_sources.endpoint IS 'Endpoint of the price source'")
    op.execute("COMMENT ON COLUMN python-api.price_sources.url IS 'URL of the price source'")
    op.execute("COMMENT ON COLUMN python-api.price_sources.element IS 'Data related to the HTML element for extracting prices'")
    op.execute("COMMENT ON COLUMN python-api.price_sources.is_active IS 'Flag to determine if source is being used by API call'")


def downgrade():
    op.drop_table('price_sources', schema='python-api')
